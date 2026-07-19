package com.company.scopery.modules.airecommendation.application.orchestrator;

import com.company.scopery.modules.airecommendation.application.port.RecommendationDetector;
import com.company.scopery.modules.airecommendation.application.port.RecommendationSourceVersionResolver;
import com.company.scopery.modules.airecommendation.application.service.RecommendationDeduplicationService;
import com.company.scopery.modules.airecommendation.domain.enums.AccessValidationResult;
import com.company.scopery.modules.airecommendation.domain.enums.BaselineImpact;
import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceLabel;
import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceMethod;
import com.company.scopery.modules.airecommendation.domain.enums.EvidenceType;
import com.company.scopery.modules.airecommendation.domain.enums.RunStatus;
import com.company.scopery.modules.airecommendation.domain.enums.SourceSystem;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.enums.SupportStrength;
import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationPolicy;
import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationRun;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionEvidence;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionItem;
import com.company.scopery.modules.airecommendation.domain.model.RecommendationDetectorDefinition;
import com.company.scopery.modules.airecommendation.domain.repository.AiRecommendationRunRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionEvidenceRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionItemRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionSuppressionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.RecommendationDetectorDefinitionRepository;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationEntityTypes;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RecommendationRunOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(RecommendationRunOrchestrator.class);
    private static final String SOURCE_SYSTEM = "SCOPERY_AI_RECOMMENDATION";
    private static final String EVENT_GENERATED = "AI_SUGGESTION_GENERATED";
    private static final String EVENT_DEDUPLICATED = "AI_SUGGESTION_DEDUPLICATED";
    private static final String EVENT_RUN_COMPLETED = "AI_RECOMMENDATION_RUN_COMPLETED";
    private static final BigDecimal MIN_CONFIDENCE = new BigDecimal("0.40");
    private static final int DEFAULT_EXPIRY_MINUTES = 10080; // 7 days

    private final Map<String, RecommendationDetector> detectorsByCode;
    private final RecommendationDetectorDefinitionRepository detectorDefinitionRepository;
    private final AiSuggestionRepository suggestionRepository;
    private final AiSuggestionItemRepository itemRepository;
    private final AiSuggestionEvidenceRepository evidenceRepository;
    private final AiSuggestionSuppressionRepository suppressionRepository;
    private final AiRecommendationRunRepository runRepository;
    private final RecommendationDeduplicationService deduplicationService;
    private final RecommendationSourceVersionResolver versionResolver;
    private final TransactionalOutboxService outbox;

    public RecommendationRunOrchestrator(List<RecommendationDetector> detectors,
                                          RecommendationDetectorDefinitionRepository detectorDefinitionRepository,
                                          AiSuggestionRepository suggestionRepository,
                                          AiSuggestionItemRepository itemRepository,
                                          AiSuggestionEvidenceRepository evidenceRepository,
                                          AiSuggestionSuppressionRepository suppressionRepository,
                                          AiRecommendationRunRepository runRepository,
                                          RecommendationDeduplicationService deduplicationService,
                                          RecommendationSourceVersionResolver versionResolver,
                                          TransactionalOutboxService outbox) {
        this.detectorsByCode = detectors.stream()
                .collect(Collectors.toMap(RecommendationDetector::detectorCode, Function.identity()));
        this.detectorDefinitionRepository = detectorDefinitionRepository;
        this.suggestionRepository = suggestionRepository;
        this.itemRepository = itemRepository;
        this.evidenceRepository = evidenceRepository;
        this.suppressionRepository = suppressionRepository;
        this.runRepository = runRepository;
        this.deduplicationService = deduplicationService;
        this.versionResolver = versionResolver;
        this.outbox = outbox;
    }

    @Transactional
    public void orchestrate(AiRecommendationRun run, AiRecommendationPolicy policy, List<String> packCodes) {
        // Transition run to RUNNING
        AiRecommendationRun running = withRunStatus(run, RunStatus.RUNNING, OffsetDateTime.now(), null);
        running = runRepository.save(running);

        int candidateCount = 0;
        int persistedCount = 0;
        int deduplicatedCount = 0;
        int suppressedCount = 0;
        int discardedCount = 0;
        int failedDetectorCount = 0;
        List<String> activeDetectorCodes = new ArrayList<>();
        boolean anyPartial = false;

        for (String packCode : packCodes) {
            List<RecommendationDetectorDefinition> detectors =
                    detectorDefinitionRepository.findAllActiveByPackCode(packCode);

            for (RecommendationDetectorDefinition detectorDef : detectors) {
                activeDetectorCodes.add(detectorDef.code());
                RecommendationDetector detector = detectorsByCode.get(detectorDef.code());

                if (detector == null) {
                    log.warn("No detector implementation found for code={}", detectorDef.code());
                    failedDetectorCount++;
                    anyPartial = true;
                    continue;
                }

                List<RecommendationDetector.SuggestionCandidate> candidates;
                try {
                    RecommendationDetector.DetectorContext ctx = new RecommendationDetector.DetectorContext(
                            run.workspaceId(), run.projectId(), run.requestedBy(), detectorDef, run.traceId());
                    candidates = detector.detect(ctx);
                } catch (Exception e) {
                    log.error("Detector {} failed: {}", detectorDef.code(), e.getMessage(), e);
                    failedDetectorCount++;
                    anyPartial = true;
                    continue;
                }

                for (RecommendationDetector.SuggestionCandidate candidate : candidates) {
                    candidateCount++;

                    // Discard below minimum confidence
                    if (candidate.confidenceValue() == null
                            || candidate.confidenceValue().compareTo(MIN_CONFIDENCE) < 0) {
                        discardedCount++;
                        continue;
                    }

                    // Check active suppression
                    boolean suppressed = suppressionRepository.hasActiveSuppressionFor(
                            run.workspaceId(), run.projectId(), run.requestedBy(),
                            candidate.suggestionType(), candidate.targetEntityType(), candidate.targetEntityId());
                    if (suppressed) {
                        suppressedCount++;
                        continue;
                    }

                    // Compute dedup key
                    String canonicalJson = deduplicationService.computeCanonicalJson(
                            run.workspaceId(), run.projectId(), candidate.suggestionType(),
                            candidate.targetEntityType(), candidate.targetEntityId(),
                            candidate.schemaCode(), candidate.schemaVersion(), candidate.proposedPayload());
                    String dedupKey = deduplicationService.computeDedupKey(canonicalJson);
                    String payloadHash = deduplicationService.computePayloadHash(
                            canonicalJson.getBytes(java.nio.charset.StandardCharsets.UTF_8));

                    // Dedup check
                    var existing = suggestionRepository.findActiveByWorkspaceAndDedupKey(run.workspaceId(), dedupKey);
                    if (existing.isPresent()) {
                        deduplicatedCount++;
                        outbox.enqueue(AiRecommendationEntityTypes.SUGGESTION, existing.get().id(),
                                EVENT_DEDUPLICATED, SOURCE_SYSTEM, 1,
                                Map.of("dedupKey", dedupKey, "runId", run.id().toString()));
                        continue;
                    }

                    // Resolve target version token
                    String targetVersionToken = null;
                    if (candidate.targetEntityId() != null) {
                        try {
                            targetVersionToken = versionResolver.resolve(
                                    candidate.targetEntityType(), candidate.targetEntityId());
                        } catch (Exception e) {
                            log.warn("Could not resolve version token for {}/{}: {}",
                                    candidate.targetEntityType(), candidate.targetEntityId(), e.getMessage());
                        }
                    }

                    ConfidenceLabel confidenceLabel = deriveLabel(candidate.confidenceValue());
                    OffsetDateTime now = OffsetDateTime.now();
                    OffsetDateTime expiresAt = now.plusMinutes(
                            detectorDef.defaultExpiryMinutes() > 0
                                    ? detectorDef.defaultExpiryMinutes()
                                    : DEFAULT_EXPIRY_MINUTES);

                    AiSuggestion suggestion = new AiSuggestion(
                            UUID.randomUUID(), run.id(), run.policyId(),
                            run.workspaceId(), run.projectId(), SourceSystem.PHASE43, null,
                            packCode, detectorDef.code(), candidate.suggestionType(),
                            candidate.schemaCode(), candidate.schemaVersion(),
                            candidate.category(), detectorDef.defaultSeverity(), SuggestionStatus.GENERATED,
                            candidate.title(), candidate.summary(), candidate.reason(),
                            candidate.targetEntityType(), candidate.targetEntityId(), targetVersionToken,
                            candidate.confidenceMethod(), candidate.confidenceValue(), confidenceLabel,
                            candidate.riskLevel(), dedupKey, payloadHash, 1,
                            null, null, null, null, null,
                            now, now, null, null, null, null, null, expiresAt, null, null,
                            now, now, 0L);

                    suggestion = suggestionRepository.save(suggestion);

                    // Persist single item (one item per candidate in MVP)
                    AiSuggestionItem item = new AiSuggestionItem(
                            UUID.randomUUID(), suggestion.id(), 0, null,
                            candidate.targetEntityType(), candidate.targetEntityId(), targetVersionToken,
                            candidate.schemaCode(), candidate.schemaVersion(),
                            candidate.proposedPayload(), null, payloadHash,
                            null, false, BaselineImpact.UNKNOWN, now);
                    itemRepository.save(item);

                    // Persist evidence facts
                    if (candidate.directEvidence() != null) {
                        List<AiSuggestionEvidence> evidences = new ArrayList<>();
                        for (int i = 0; i < candidate.directEvidence().size(); i++) {
                            RecommendationDetector.EvidenceFact fact = candidate.directEvidence().get(i);
                            evidences.add(new AiSuggestionEvidence(
                                    UUID.randomUUID(), suggestion.id(), i,
                                    EvidenceType.DOMAIN_FACT, SupportStrength.DIRECT,
                                    null, null, null,
                                    fact.sourceType(), fact.sourceRefId(), null, null,
                                    fact.title(), fact.fragment(), fact.appRoute(),
                                    null, AccessValidationResult.ALLOWED, now, now));
                        }
                        evidenceRepository.saveAll(evidences);
                    }

                    outbox.enqueue(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                            EVENT_GENERATED, SOURCE_SYSTEM, 1,
                            Map.of("suggestionRef", "p43:" + suggestion.id(),
                                    "runId", run.id().toString(),
                                    "packCode", packCode,
                                    "detectorCode", detectorDef.code()));

                    persistedCount++;
                }
            }
        }

        // Complete run
        RunStatus finalStatus = anyPartial ? RunStatus.PARTIAL : RunStatus.SUCCEEDED;
        AiRecommendationRun completed = withRunCompleted(running, finalStatus, activeDetectorCodes,
                candidateCount, persistedCount, deduplicatedCount, suppressedCount,
                discardedCount, failedDetectorCount, OffsetDateTime.now(), null);
        runRepository.save(completed);

        outbox.enqueue(AiRecommendationEntityTypes.RUN, run.id(),
                EVENT_RUN_COMPLETED, SOURCE_SYSTEM, 1,
                Map.of("runId", run.id().toString(), "status", finalStatus.name(),
                        "persisted", persistedCount, "deduplicated", deduplicatedCount));
    }

    private ConfidenceLabel deriveLabel(BigDecimal value) {
        if (value == null) return ConfidenceLabel.LOW;
        if (value.compareTo(new BigDecimal("0.85")) >= 0) return ConfidenceLabel.HIGH;
        if (value.compareTo(new BigDecimal("0.65")) >= 0) return ConfidenceLabel.MEDIUM;
        return ConfidenceLabel.LOW;
    }

    private AiRecommendationRun withRunStatus(AiRecommendationRun r, RunStatus status,
                                               OffsetDateTime startedAt, String errorCode) {
        return new AiRecommendationRun(r.id(), r.policyId(), r.workspaceId(), r.projectId(),
                r.requestedBy(), r.triggerType(), r.idempotencyKey(), r.requestHash(),
                status, r.requestedPackCodes(), r.detectorCodes(),
                r.originConversationId(), r.originMessageId(), r.originTurnId(),
                r.detectorCount(), r.candidateCount(), r.persistedCount(), r.deduplicatedCount(),
                r.suppressedCount(), r.discardedCount(), r.failedDetectorCount(),
                r.latencyMs(), errorCode, r.errorSummaryRedacted(), r.traceId(),
                startedAt != null ? startedAt : r.startedAt(), r.completedAt(),
                r.createdAt(), OffsetDateTime.now(), r.version() + 1);
    }

    private AiRecommendationRun withRunCompleted(AiRecommendationRun r, RunStatus status,
                                                   List<String> detectorCodes,
                                                   int candidates, int persisted,
                                                   int deduplicated, int suppressed,
                                                   int discarded, int failedDetectors,
                                                   OffsetDateTime completedAt, String errorCode) {
        OffsetDateTime now = OffsetDateTime.now();
        Integer latencyMs = r.startedAt() != null
                ? (int) java.time.Duration.between(r.startedAt(), completedAt).toMillis()
                : null;
        return new AiRecommendationRun(r.id(), r.policyId(), r.workspaceId(), r.projectId(),
                r.requestedBy(), r.triggerType(), r.idempotencyKey(), r.requestHash(),
                status, r.requestedPackCodes(), detectorCodes,
                r.originConversationId(), r.originMessageId(), r.originTurnId(),
                detectorCodes.size(), candidates, persisted, deduplicated,
                suppressed, discarded, failedDetectors,
                latencyMs, errorCode, null, r.traceId(),
                r.startedAt(), completedAt, r.createdAt(), now, r.version() + 1);
    }
}
