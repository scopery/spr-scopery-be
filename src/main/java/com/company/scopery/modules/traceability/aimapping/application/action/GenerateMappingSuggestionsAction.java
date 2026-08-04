package com.company.scopery.modules.traceability.aimapping.application.action;

import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.traceability.aimapping.application.command.GenerateMappingCommand;
import com.company.scopery.modules.traceability.aimapping.application.internal.MappingCandidateRetrievalService;
import com.company.scopery.modules.traceability.aimapping.application.internal.MappingCandidateRetrievalService.CandidateResult;
import com.company.scopery.modules.traceability.aimapping.application.internal.MappingCandidateRetrievalService.UnmappedSource;
import com.company.scopery.modules.traceability.aimapping.application.internal.MappingPromptBuilderService;
import com.company.scopery.modules.traceability.aimapping.application.internal.MappingPromptBuilderService.ScoredCandidate;
import com.company.scopery.modules.traceability.aimapping.application.internal.MappingPromptResolverService;
import com.company.scopery.modules.traceability.aimapping.application.internal.MappingSummaryBuilderService;
import com.company.scopery.modules.traceability.aimapping.application.response.MappingRunResponse;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRunStatus;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingScope;
import com.company.scopery.modules.traceability.aimapping.run.domain.model.MappingRun;
import com.company.scopery.modules.traceability.aimapping.run.domain.model.MappingRunRepository;
import com.company.scopery.modules.traceability.aimapping.shared.activity.AiMappingActivityLogger;
import com.company.scopery.modules.traceability.aimapping.shared.config.AiMappingProperties;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingActivityActions;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingEntityTypes;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingPromptKeys;
import com.company.scopery.modules.traceability.aimapping.shared.error.AiMappingExceptions;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.ConfidenceBand;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionDecision;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionReviewStatus;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestion;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestionRepository;
import com.company.scopery.modules.traceability.aimapping.summary.domain.model.MappingSummary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GenerateMappingSuggestionsAction {

    private static final Logger log = LoggerFactory.getLogger(GenerateMappingSuggestionsAction.class);
    private static final BigDecimal HIGH_SCORE_THRESHOLD   = new BigDecimal("0.75");
    private static final BigDecimal HIGH_MARGIN_THRESHOLD  = new BigDecimal("0.08");
    private static final BigDecimal MEDIUM_SCORE_THRESHOLD = new BigDecimal("0.55");
    private static final BigDecimal MEDIUM_MARGIN_THRESHOLD = new BigDecimal("0.04");

    // CLAUDE.md §21 documented exception: no @Transactional because AI provider calls can
    // take seconds and must not hold a DB transaction open. Run status transitions are each
    // committed in their own transaction via saveAndFlush inside the repositories.
    private final MappingRunRepository runRepository;
    private final MappingSuggestionRepository suggestionRepository;
    private final ModelDeploymentRepository deploymentRepository;
    private final AiModelRepository aiModelRepository;
    private final ProviderRepository providerRepository;
    private final AiProviderAdapterRegistry adapterRegistry;
    private final MappingSummaryBuilderService summaryBuilderService;
    private final MappingCandidateRetrievalService candidateRetrievalService;
    private final MappingPromptBuilderService promptBuilderService;
    private final MappingPromptResolverService promptResolverService;
    private final AiMappingProperties properties;
    private final AiMappingActivityLogger activityLogger;
    private final ObjectMapper objectMapper;
    private final TaskExecutor taskExecutor;

    public GenerateMappingSuggestionsAction(
            MappingRunRepository runRepository,
            MappingSuggestionRepository suggestionRepository,
            ModelDeploymentRepository deploymentRepository,
            AiModelRepository aiModelRepository,
            ProviderRepository providerRepository,
            AiProviderAdapterRegistry adapterRegistry,
            MappingSummaryBuilderService summaryBuilderService,
            MappingCandidateRetrievalService candidateRetrievalService,
            MappingPromptBuilderService promptBuilderService,
            MappingPromptResolverService promptResolverService,
            AiMappingProperties properties,
            AiMappingActivityLogger activityLogger,
            ObjectMapper objectMapper,
            @Qualifier("aiMappingTaskExecutor") TaskExecutor taskExecutor) {
        this.runRepository = runRepository;
        this.suggestionRepository = suggestionRepository;
        this.deploymentRepository = deploymentRepository;
        this.aiModelRepository = aiModelRepository;
        this.providerRepository = providerRepository;
        this.adapterRegistry = adapterRegistry;
        this.summaryBuilderService = summaryBuilderService;
        this.candidateRetrievalService = candidateRetrievalService;
        this.promptBuilderService = promptBuilderService;
        this.promptResolverService = promptResolverService;
        this.properties = properties;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
        this.taskExecutor = taskExecutor;
    }

    /**
     * Starts a mapping run and returns immediately with status=RUNNING.
     * Sources are processed asynchronously; clients poll GET …/runs/{runId}
     * for processedSourceCount / sourceCount progress.
     */
    public MappingRunResponse execute(GenerateMappingCommand command) {
        if (runRepository.existsByProjectIdAndRelationTypeAndStatus(
                command.projectId(), command.relationType(), MappingRunStatus.RUNNING)) {
            throw AiMappingExceptions.runStillRunning(command.projectId(), command.relationType().name());
        }

        ModelDeployment deployment;
        if (command.modelDeploymentId() != null) {
            deployment = deploymentRepository.findById(command.modelDeploymentId())
                    .orElseThrow(AiMappingExceptions::noDeploymentConfigured);
        } else {
            deployment = deploymentRepository.findDefault()
                    .orElseThrow(AiMappingExceptions::noDeploymentConfigured);
        }
        AiModel aiModel = aiModelRepository.findById(deployment.modelId())
                .orElseThrow(AiMappingExceptions::noDeploymentConfigured);
        Provider provider = providerRepository.findById(aiModel.providerId())
                .orElseThrow(AiMappingExceptions::noDeploymentConfigured);
        adapterRegistry.getAdapter(provider.code().value());

        String promptCode = resolvePromptCode(command.relationType());
        promptResolverService.resolveByTemplateCode(promptCode);

        MappingRun run = MappingRun.create(
                command.projectId(), command.relationType(),
                command.scope() != null ? command.scope() : MappingScope.UNMAPPED,
                deployment.id(), promptCode, properties.getCandidateLimit(), command.requestedBy());
        run = runRepository.save(run);

        List<UnmappedSource> sources;
        try {
            sources = candidateRetrievalService.findUnmappedSources(
                    command.relationType(), command.projectId(), properties.getBatchSize() * 10);
        } catch (Exception e) {
            log.error("Failed to find unmapped sources for run {}: {}", run.id(), e.getMessage(), e);
            run = persistProgress(run, MappingRunStatus.FAILED, 0, 0, 0, null);
            return MappingRunResponse.from(run);
        }

        if (sources.isEmpty()) {
            run = persistProgress(run, MappingRunStatus.COMPLETED, 0, 0, 0, buildTokenUsageJson(0, 0));
            activityLogger.logSuccess(
                    AiMappingEntityTypes.MAPPING_RUN, run.id(),
                    AiMappingActivityActions.GENERATE_MAPPING_RUN,
                    "Generated 0 suggestions (no unmapped sources) for " + command.relationType().name());
            return MappingRunResponse.from(run);
        }

        run = run.withProgress(MappingRunStatus.RUNNING, sources.size(), 0, 0, null, Instant.now(), null);
        run = runRepository.save(run);

        log.info("Mapping run {} accepted async: {} sources ({})", run.id(), sources.size(), command.relationType());
        final UUID runId = run.id();
        final List<UnmappedSource> sourceSnapshot = List.copyOf(sources);
        taskExecutor.execute(() -> processSourcesAsync(runId, command, sourceSnapshot));

        return MappingRunResponse.from(run);
    }

    public void processSourcesAsync(UUID runId, GenerateMappingCommand command, List<UnmappedSource> sources) {
        log.info("Mapping run {} worker started ({} sources)", runId, sources.size());
        MappingRun run = runRepository.findById(runId).orElse(null);
        if (run == null) {
            log.error("Mapping run {} disappeared before async processing", runId);
            return;
        }

        try {
            ModelDeployment deployment = deploymentRepository.findById(run.modelDeploymentId())
                    .orElseThrow(AiMappingExceptions::noDeploymentConfigured);
            AiModel aiModel = aiModelRepository.findById(deployment.modelId())
                    .orElseThrow(AiMappingExceptions::noDeploymentConfigured);
            Provider provider = providerRepository.findById(aiModel.providerId())
                    .orElseThrow(AiMappingExceptions::noDeploymentConfigured);
            AiProviderAdapter adapter = adapterRegistry.getAdapter(provider.code().value());
            MappingPromptResolverService.ResolvedPrompt resolvedPrompt =
                    promptResolverService.resolveByTemplateCode(run.promptKey());

            // Warm source summaries
            log.info("Mapping run {}: warming summaries for {} sources", runId, sources.size());
            for (UnmappedSource source : sources) {
                try { summaryBuilderService.getOrBuildSummary(source.sourceType(), source.id()); }
                catch (Exception e) { log.warn("Failed to warm summary for source {}: {}", source.id(), e.getMessage()); }
            }

            // Warm target summaries so vector retrieval finds them
            var allTargets = candidateRetrievalService.findAllEligibleTargets(command.relationType(), command.projectId());
            log.info("Mapping run {}: warming summaries for {} eligible targets", runId, allTargets.size());
            for (var target : allTargets) {
                try { summaryBuilderService.getOrBuildSummary(target.targetType(), target.id()); }
                catch (Exception e) { log.warn("Failed to warm summary for target {}: {}", target.id(), e.getMessage()); }
            }

            // ---------------------------------------------------------------
            // Phase A — Pre-screening (no AI call)
            // ---------------------------------------------------------------
            List<UnmappedSource> ambiguous = new ArrayList<>();
            int clearMatchCount = 0;
            int noCandidateCount = 0;

            Map<UUID, MappingSummary>           sourceSummaryMap    = new LinkedHashMap<>();
            Map<UUID, List<ScoredCandidate>>    sourceCandidateMap  = new LinkedHashMap<>();

            for (UnmappedSource source : sources) {
                MappingSummary sourceSummary = summaryBuilderService.getOrBuildSummary(
                        source.sourceType(), source.id());
                if (sourceSummary == null) {
                    log.warn("Skipping source {} ({}): summary unavailable", source.id(), source.sourceType());
                    continue;
                }

                List<CandidateResult> rawCandidates = candidateRetrievalService.findCandidates(
                        command.relationType(), command.projectId(),
                        source.id(), source.searchText(), properties.getCandidateLimit());

                if (rawCandidates.isEmpty()) {
                    suggestionRepository.save(buildNoMatchSuggestion(source, run));
                    noCandidateCount++;
                    continue;
                }

                List<ScoredCandidate> scored = new ArrayList<>();
                for (CandidateResult cr : rawCandidates) {
                    MappingSummary cs = summaryBuilderService.getOrBuildSummary(cr.targetType(), cr.entityId());
                    if (cs != null) scored.add(new ScoredCandidate(cs, cr.retrievalScore(), cr.rank(), cr.titleSimilarity()));
                }

                if (scored.isEmpty()) {
                    suggestionRepository.save(buildNoMatchSuggestion(source, run));
                    noCandidateCount++;
                    continue;
                }

                // Best candidate is first (highest RRF score)
                ScoredCandidate best = scored.get(0);
                if (best.titleSimilarity() >= properties.getClearMatchThreshold()) {
                    MappingSuggestion autoSuggest = buildClearMatchSuggestion(source, run, best);
                    suggestionRepository.save(autoSuggest);
                    clearMatchCount++;
                    continue;
                }

                sourceSummaryMap.put(source.id(), sourceSummary);
                sourceCandidateMap.put(source.id(), scored);
                ambiguous.add(source);
            }

            log.info("Mapping run {} pre-screen: {} clear-match, {} no-candidate, {} ambiguous",
                    runId, clearMatchCount, noCandidateCount, ambiguous.size());

            // ---------------------------------------------------------------
            // Phase B — Batch AI calls for ambiguous sources
            // ---------------------------------------------------------------
            int totalSuggestions = clearMatchCount + noCandidateCount;
            int processed = sources.size() - ambiguous.size();
            int batchSize = properties.getBatchSize();

            for (int i = 0; i < ambiguous.size(); i += batchSize) {
                List<UnmappedSource> chunk = ambiguous.subList(i, Math.min(i + batchSize, ambiguous.size()));
                int chunkNum = (i / batchSize) + 1;
                int totalChunks = (int) Math.ceil((double) ambiguous.size() / batchSize);
                log.info("Mapping run {}: batch AI call {}/{} with {} sources", runId, chunkNum, totalChunks, chunk.size());

                Map<UUID, MappingSummary> chunkSummaries = new LinkedHashMap<>();
                Map<UUID, List<ScoredCandidate>> chunkCandidates = new LinkedHashMap<>();
                for (UnmappedSource s : chunk) {
                    chunkSummaries.put(s.id(), sourceSummaryMap.get(s.id()));
                    chunkCandidates.put(s.id(), sourceCandidateMap.get(s.id()));
                }

                String inputJson = promptBuilderService.buildBatchInputJson(chunk, chunkSummaries, chunkCandidates);
                String fullPrompt = renderPrompt(resolvedPrompt, inputJson);

                AiProviderRequest aiRequest = new AiProviderRequest(
                        provider.id(), aiModel.providerModelId(), fullPrompt,
                        resolvedPrompt.temperature(),
                        resolvedPrompt.maxTokens() != null ? resolvedPrompt.maxTokens() : 4096);

                AiProviderResponse aiResponse;
                try {
                    aiResponse = adapter.call(aiRequest);
                } catch (Exception e) {
                    log.error("AI batch call failed for chunk {}/{} in run {}: {}", chunkNum, totalChunks, runId, e.getMessage());
                    for (UnmappedSource s : chunk) {
                        suggestionRepository.save(buildNoMatchSuggestion(s, run));
                        totalSuggestions++;
                    }
                    processed += chunk.size();
                    run = persistProgress(run, MappingRunStatus.RUNNING, sources.size(), processed, totalSuggestions, null);
                    continue;
                }

                Map<UUID, List<Map<String, Object>>> batchResults = parseBatchAiResponse(aiResponse.outputText());

                for (UnmappedSource s : chunk) {
                    List<Map<String, Object>> aiResults = batchResults.getOrDefault(s.id(), List.of());
                    List<ScoredCandidate> scored = sourceCandidateMap.get(s.id());
                    List<MappingSuggestion> suggestions = buildSuggestions(s, run, scored, aiResults);
                    if (suggestions.isEmpty()) {
                        suggestionRepository.save(buildNoMatchSuggestion(s, run));
                        totalSuggestions++;
                    } else {
                        suggestionRepository.saveAll(suggestions);
                        totalSuggestions += suggestions.size();
                    }
                }

                processed += chunk.size();
                run = persistProgress(run, MappingRunStatus.RUNNING, sources.size(), processed, totalSuggestions, null);
            }

            run = persistProgress(run, MappingRunStatus.COMPLETED, sources.size(), processed, totalSuggestions,
                    buildTokenUsageJson(0, 0));
            activityLogger.logSuccess(
                    AiMappingEntityTypes.MAPPING_RUN, run.id(),
                    AiMappingActivityActions.GENERATE_MAPPING_RUN,
                    "Generated " + totalSuggestions + " suggestions for " + command.relationType().name());
        } catch (Exception e) {
            log.error("Async mapping run {} failed: {}", runId, e.getMessage(), e);
            try {
                MappingRun current = runRepository.findById(runId).orElse(run);
                persistProgress(current, MappingRunStatus.FAILED,
                        current.sourceCount(), current.processedSourceCount(), current.suggestionCount(), null);
            } catch (Exception persistErr) {
                log.error("Failed to mark mapping run {} as FAILED: {}", runId, persistErr.getMessage());
            }
        }
    }

    private MappingRun persistProgress(MappingRun run, MappingRunStatus status,
                                        Integer sourceCount, int processedSourceCount,
                                        Integer suggestionCount, String tokenUsage) {
        Instant startedAt = run.startedAt() != null ? run.startedAt()
                : (status == MappingRunStatus.RUNNING || status == MappingRunStatus.COMPLETED ? Instant.now() : null);
        Instant completedAt = (status == MappingRunStatus.COMPLETED || status == MappingRunStatus.FAILED)
                ? Instant.now() : run.completedAt();
        return runRepository.save(run.withProgress(
                status,
                sourceCount != null ? sourceCount : run.sourceCount(),
                processedSourceCount,
                suggestionCount != null ? suggestionCount : run.suggestionCount(),
                tokenUsage != null ? tokenUsage : run.tokenUsageJson(),
                startedAt, completedAt));
    }

    // -------------------------------------------------------------------------
    // Batch AI response parsing — returns sourceId → list of suggestion maps
    // -------------------------------------------------------------------------

    /**
     * Parses batch AI response of shape:
     * { "results": [ { "sourceId": "uuid", "decision": "SUGGEST|NO_MATCH", "suggestions": [{...}] } ] }
     *
     * Returns map: sourceId UUID → flat list of suggestion maps (each has "targetId", etc).
     * Sources with decision=NO_MATCH are mapped to empty list.
     */
    private Map<UUID, List<Map<String, Object>>> parseBatchAiResponse(String outputText) {
        Map<UUID, List<Map<String, Object>>> result = new HashMap<>();
        if (outputText == null || outputText.isBlank()) return result;
        String json = extractJson(outputText);
        try {
            Object parsed = objectMapper.readValue(json, Object.class);
            if (!(parsed instanceof Map<?, ?> root)) return result;
            if (!root.containsKey("results")) return result;

            List<Map<String, Object>> results =
                    objectMapper.convertValue(root.get("results"), new TypeReference<>() {});

            for (Map<String, Object> entry : results) {
                String sourceIdStr = getString(entry, "sourceId");
                if (sourceIdStr == null) continue;
                UUID sourceId;
                try { sourceId = UUID.fromString(sourceIdStr); } catch (Exception e) { continue; }

                String outerDecision = getString(entry, "decision");
                if ("NO_MATCH".equalsIgnoreCase(outerDecision)) {
                    result.put(sourceId, List.of());
                    continue;
                }

                Object nested = entry.get("suggestions");
                if (nested instanceof List<?> sugList) {
                    List<Map<String, Object>> flattened = new ArrayList<>();
                    for (Object item : sugList) {
                        if (item instanceof Map<?, ?> itemMap) {
                            Map<String, Object> copy = objectMapper.convertValue(itemMap, new TypeReference<>() {});
                            if (!copy.containsKey("decision") && outerDecision != null) {
                                copy.put("decision", outerDecision);
                            }
                            flattened.add(copy);
                        }
                    }
                    result.put(sourceId, flattened);
                } else {
                    // Flat result (legacy V1 UC/TC schema — targetId at same level as sourceId)
                    if (entry.containsKey("targetId")) {
                        result.put(sourceId, List.of(new LinkedHashMap<>(entry)));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse batch AI response JSON: {}", e.getMessage());
        }
        return result;
    }

    private String extractJson(String text) {
        int start = text.indexOf('[');
        int startObj = text.indexOf('{');
        if (start == -1 || (startObj != -1 && startObj < start)) {
            start = startObj;
            int end = text.lastIndexOf('}');
            return end > start ? text.substring(start, end + 1) : text;
        }
        int end = text.lastIndexOf(']');
        return end > start ? text.substring(start, end + 1) : text;
    }

    // -------------------------------------------------------------------------
    // Suggestion builders
    // -------------------------------------------------------------------------

    private List<MappingSuggestion> buildSuggestions(UnmappedSource source, MappingRun run,
                                                       List<ScoredCandidate> candidates,
                                                       List<Map<String, Object>> aiResults) {
        List<MappingSuggestion> suggestions = new ArrayList<>();
        for (Map<String, Object> result : aiResults) {
            String targetIdStr = getString(result, "targetId");
            if (targetIdStr == null) continue;

            ScoredCandidate matched = candidates.stream()
                    .filter(c -> c.summary().entityId().toString().equals(targetIdStr))
                    .findFirst().orElse(null);
            if (matched == null) continue;

            // V2 REQUIREMENT_TO_FUNCTION uses MATCH/PARTIAL; others use SUGGEST/AMBIGUOUS/NO_MATCH
            String decisionStr = getString(result, "decision");
            SuggestionDecision decision = decisionStr != null ? safeDecision(decisionStr) : SuggestionDecision.SUGGEST;
            if (decision == SuggestionDecision.NO_MATCH) continue;

            BigDecimal aiScoreRaw = getBigDecimal(result, "aiScore");
            if (aiScoreRaw == null) aiScoreRaw = getBigDecimal(result, "score");
            final BigDecimal aiScore = aiScoreRaw;
            BigDecimal secondBestScore = getBigDecimal(result, "secondBestScore");
            final BigDecimal scoreMargin = aiScore != null && secondBestScore != null
                    ? aiScore.subtract(secondBestScore) : null;

            ConfidenceBand confidence = parseConfidenceBand(getString(result, "confidenceBand"))
                    .orElseGet(() -> calculateConfidenceBand(aiScore, scoreMargin, toJson(result.get("warnings"))));

            // V1 fields → mapped to DB columns
            String evidenceJson    = coalesce(toJson(result.get("coveredByFunction")),  toJson(result.get("evidence")));
            String warningsJson    = coalesce(toJson(result.get("notCovered")),          toJson(result.get("warnings")));
            String reasonCodesJson = coalesce(toJson(result.get("conflicts")),           toJson(result.get("reasonCodes")));
            String coverageJson    = result.containsKey("requirementIntent")
                    ? toJson(result.get("requirementIntent"))
                    : toJson(result.get("coverageContribution"));

            suggestions.add(new MappingSuggestion(
                    UUID.randomUUID(), run.id(),
                    source.sourceType(), source.id(), source.entityVersion(), null,
                    matched.summary().entityType(), matched.summary().entityId(),
                    matched.summary().entityVersion(), null,
                    run.relationType(), matched.rank(),
                    BigDecimal.valueOf(matched.retrievalScore()), aiScore,
                    aiScore, secondBestScore, scoreMargin,
                    confidence, decision,
                    reasonCodesJson, evidenceJson, warningsJson, coverageJson,
                    SuggestionReviewStatus.PENDING, null, null,
                    Instant.now(), Instant.now()
            ));
        }
        return suggestions;
    }

    private MappingSuggestion buildNoMatchSuggestion(UnmappedSource source, MappingRun run) {
        return new MappingSuggestion(
                UUID.randomUUID(), run.id(),
                source.sourceType(), source.id(), source.entityVersion(), null,
                null, null, null, null,
                run.relationType(), null,
                null, null, null, null, null,
                ConfidenceBand.LOW, SuggestionDecision.NO_MATCH,
                null, null, null, null,
                SuggestionReviewStatus.PENDING, null, null,
                Instant.now(), Instant.now()
        );
    }

    private MappingSuggestion buildClearMatchSuggestion(UnmappedSource source, MappingRun run,
                                                          ScoredCandidate best) {
        return new MappingSuggestion(
                UUID.randomUUID(), run.id(),
                source.sourceType(), source.id(), source.entityVersion(), null,
                best.summary().entityType(), best.summary().entityId(),
                best.summary().entityVersion(), null,
                run.relationType(), best.rank(),
                BigDecimal.valueOf(best.retrievalScore()), null,
                null, null, null,
                ConfidenceBand.HIGH, SuggestionDecision.SUGGEST,
                null, null, null, null,
                SuggestionReviewStatus.PENDING, null, null,
                Instant.now(), Instant.now()
        );
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private java.util.Optional<ConfidenceBand> parseConfidenceBand(String raw) {
        if (raw == null || raw.isBlank()) return java.util.Optional.empty();
        try {
            return java.util.Optional.of(ConfidenceBand.valueOf(raw.trim().toUpperCase()));
        } catch (Exception e) {
            return java.util.Optional.empty();
        }
    }

    private ConfidenceBand calculateConfidenceBand(BigDecimal finalScore, BigDecimal scoreMargin, String warnings) {
        if (finalScore == null) return ConfidenceBand.LOW;
        boolean hasWarnings = warnings != null && !warnings.isBlank() && !warnings.equals("[]") && !warnings.equals("null");
        if (finalScore.compareTo(HIGH_SCORE_THRESHOLD) >= 0
                && scoreMargin != null && scoreMargin.compareTo(HIGH_MARGIN_THRESHOLD) >= 0
                && !hasWarnings) {
            return ConfidenceBand.HIGH;
        }
        if (finalScore.compareTo(MEDIUM_SCORE_THRESHOLD) >= 0
                || (scoreMargin != null && scoreMargin.compareTo(MEDIUM_MARGIN_THRESHOLD) >= 0)) {
            return ConfidenceBand.MEDIUM;
        }
        return ConfidenceBand.LOW;
    }

    private String renderPrompt(MappingPromptResolverService.ResolvedPrompt resolved, String inputJson) {
        String userPart = resolved.userPromptTemplate() != null
                ? resolved.userPromptTemplate().replace("{{INPUT_JSON}}", inputJson)
                : inputJson;
        if (resolved.systemPrompt() != null && !resolved.systemPrompt().isBlank()) {
            return resolved.systemPrompt() + "\n\n" + userPart;
        }
        return userPart;
    }

    private String resolvePromptCode(com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType relationType) {
        return switch (relationType) {
            case REQUIREMENT_TO_FUNCTION -> AiMappingPromptKeys.REQ_FUNC_PROMPT_CODE;
            case FUNCTION_TO_USE_CASE    -> AiMappingPromptKeys.UC_FUNCTION_PROMPT_CODE;
            case USE_CASE_TO_TEST_CASE   -> AiMappingPromptKeys.TC_UC_PROMPT_CODE;
        };
    }

    private String buildTokenUsageJson(int inputTokens, int outputTokens) {
        return "{\"inputTokens\":" + inputTokens + ",\"outputTokens\":" + outputTokens + "}";
    }

    private String toJson(Object value) {
        if (value == null) return null;
        try { return objectMapper.writeValueAsString(value); }
        catch (Exception e) { return null; }
    }

    /** Returns first non-null value, or null if both are null. */
    private static String coalesce(String a, String b) {
        return a != null ? a : b;
    }

    private static String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    private static BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return null;
        try { return new BigDecimal(v.toString()); }
        catch (Exception e) { return null; }
    }

    private static SuggestionDecision safeDecision(String value) {
        return switch (value.toUpperCase()) {
            case "MATCH", "PARTIAL", "SUGGEST" -> SuggestionDecision.SUGGEST;
            case "AMBIGUOUS"                   -> SuggestionDecision.AMBIGUOUS;
            default                            -> SuggestionDecision.NO_MATCH;
        };
    }
}
