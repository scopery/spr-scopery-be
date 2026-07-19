package com.company.scopery.modules.airecommendation.application.action;

import com.company.scopery.modules.airecommendation.application.command.SuppressSuggestionCommand;
import com.company.scopery.modules.airecommendation.application.response.SuppressSuggestionResponse;
import com.company.scopery.modules.airecommendation.application.service.RecommendationDeduplicationService;
import com.company.scopery.modules.airecommendation.domain.enums.ReviewDecision;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionReview;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionSuppression;
import com.company.scopery.modules.airecommendation.domain.model.RecommendationDetectorDefinition;
import com.company.scopery.modules.airecommendation.domain.policy.RecommendationLifecyclePolicy;
import com.company.scopery.modules.airecommendation.domain.policy.SuppressionPolicy;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionReviewRepository;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionSuppressionRepository;
import com.company.scopery.modules.airecommendation.domain.repository.RecommendationDetectorDefinitionRepository;
import com.company.scopery.modules.airecommendation.domain.value.SuggestionRef;
import com.company.scopery.modules.airecommendation.shared.activity.AiRecommendationActivityLogger;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationActivityActions;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationEntityTypes;
import com.company.scopery.modules.airecommendation.shared.error.AiRecommendationExceptions;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class SuppressSuggestionAction {

    private static final String SOURCE_SYSTEM = "SCOPERY_AI_RECOMMENDATION";
    private static final String EVENT_SUPPRESSED = "AI_SUGGESTION_SUPPRESSED";

    private final AiSuggestionRepository suggestionRepository;
    private final AiSuggestionSuppressionRepository suppressionRepository;
    private final AiSuggestionReviewRepository reviewRepository;
    private final RecommendationDetectorDefinitionRepository detectorRepository;
    private final RecommendationDeduplicationService deduplicationService;
    private final TransactionalOutboxService outbox;
    private final AiRecommendationActivityLogger activityLogger;

    public SuppressSuggestionAction(AiSuggestionRepository suggestionRepository,
                                     AiSuggestionSuppressionRepository suppressionRepository,
                                     AiSuggestionReviewRepository reviewRepository,
                                     RecommendationDetectorDefinitionRepository detectorRepository,
                                     RecommendationDeduplicationService deduplicationService,
                                     TransactionalOutboxService outbox,
                                     AiRecommendationActivityLogger activityLogger) {
        this.suggestionRepository = suggestionRepository;
        this.suppressionRepository = suppressionRepository;
        this.reviewRepository = reviewRepository;
        this.detectorRepository = detectorRepository;
        this.deduplicationService = deduplicationService;
        this.outbox = outbox;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public SuppressSuggestionResponse execute(SuppressSuggestionCommand cmd) {
        SuggestionRef ref = SuggestionRef.parse(cmd.suggestionRef());

        if (ref.isPhase21()) {
            throw AiRecommendationExceptions.legacySuggestionSuppressionUnavailable(cmd.suggestionRef());
        }

        AiSuggestion suggestion = suggestionRepository.findById(ref.uuid())
                .filter(s -> cmd.workspaceId().equals(s.workspaceId()))
                .orElseThrow(() -> AiRecommendationExceptions.suggestionNotFound(cmd.suggestionRef()));

        if (cmd.expectedVersion() >= 0 && suggestion.version() != cmd.expectedVersion()) {
            throw AiRecommendationExceptions.suggestionVersionConflict(
                    cmd.suggestionRef(), cmd.expectedVersion(), suggestion.version());
        }

        if (!RecommendationLifecyclePolicy.canTransitionTo(suggestion.status(), SuggestionStatus.SUPPRESSED)) {
            throw AiRecommendationExceptions.suggestionInvalidStatus(
                    cmd.suggestionRef(), suggestion.status().name(), "SUPPRESS");
        }

        SuppressionPolicy.validateDuration(cmd.durationDays());

        // Check non-suppressible flag on detector definition
        if (suggestion.detectorCode() != null) {
            detectorRepository.findActiveByCode(suggestion.detectorCode()).ifPresent(detector -> {
                if (detector.nonSuppressible()) {
                    throw AiRecommendationExceptions.suppressionForbidden(cmd.suggestionRef());
                }
            });
        }

        String suppressionKey = deduplicationService.computeSuppressionKey(
                cmd.workspaceId(), suggestion.projectId(), cmd.actorId(),
                cmd.scopeType().name(), buildScopeKey(suggestion, cmd));

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = now.plusDays(cmd.durationDays());

        AiSuggestionSuppression suppression = new AiSuggestionSuppression(
                UUID.randomUUID(), cmd.workspaceId(), suggestion.projectId(), cmd.actorId(),
                cmd.scopeType(), buildScopeKey(suggestion, cmd), suppressionKey,
                suggestion.targetEntityType(), suggestion.targetEntityId(),
                suggestion.suggestionType(), suggestion.packCode(),
                cmd.reasonCode(), cmd.comment(), true, now, expiresAt, null, now, now, 0L);
        suppression = suppressionRepository.save(suppression);

        AiSuggestion updated = withStatus(suggestion, SuggestionStatus.SUPPRESSED, now);
        updated = suggestionRepository.save(updated);

        reviewRepository.save(new AiSuggestionReview(
                UUID.randomUUID(), suggestion.id(), cmd.actorId(),
                ReviewDecision.SUPPRESS, suggestion.status(), SuggestionStatus.SUPPRESSED,
                cmd.expectedVersion(), cmd.reasonCode(), cmd.comment(), null, cmd.traceId(), now));

        outbox.enqueue(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                EVENT_SUPPRESSED, SOURCE_SYSTEM, 1,
                Map.of("suggestionRef", cmd.suggestionRef(), "suppressionId", suppression.id().toString()));

        activityLogger.logSuccess(AiRecommendationEntityTypes.SUGGESTION, suggestion.id(),
                AiRecommendationActivityActions.SUPPRESS_SUGGESTION, "Suggestion suppressed");

        return new SuppressSuggestionResponse(cmd.suggestionRef(), updated.status().name(),
                suppression.id(), cmd.scopeType().name(), expiresAt, updated.version());
    }

    private String buildScopeKey(AiSuggestion suggestion, SuppressSuggestionCommand cmd) {
        return switch (cmd.scopeType()) {
            case TARGET -> suggestion.targetEntityType() + ":" + suggestion.targetEntityId();
            case TYPE -> suggestion.suggestionType();
            case PACK -> suggestion.packCode();
        };
    }

    private AiSuggestion withStatus(AiSuggestion s, SuggestionStatus status, OffsetDateTime now) {
        return new AiSuggestion(
                s.id(), s.runId(), s.policyId(), s.workspaceId(), s.projectId(), s.sourceSystem(),
                s.legacyPhase21SuggestionId(), s.packCode(), s.detectorCode(), s.suggestionType(),
                s.schemaCode(), s.schemaVersion(), s.category(), s.severity(), status,
                s.title(), s.summary(), s.reason(), s.targetEntityType(), s.targetEntityId(),
                s.targetVersionToken(), s.confidenceMethod(), s.confidenceValue(), s.confidenceLabel(),
                s.riskLevel(), s.dedupKey(), s.payloadHash(), s.occurrenceCount(),
                s.originConversationId(), s.originMessageId(), s.originTurnId(),
                s.supersedesSuggestionId(), s.supersededBySuggestionId(),
                s.firstObservedAt(), s.lastObservedAt(),
                s.viewedAt(), s.editedAt(), s.acceptedAt(), s.rejectedAt(), now,
                s.expiresAt(), s.staleAt(), s.staleReasonCode(), s.createdAt(), now, s.version() + 1);
    }
}
