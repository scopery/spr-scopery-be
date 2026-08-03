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
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GenerateMappingSuggestionsAction {

    private static final Logger log = LoggerFactory.getLogger(GenerateMappingSuggestionsAction.class);
    private static final BigDecimal HIGH_SCORE_THRESHOLD = new BigDecimal("0.88");
    private static final BigDecimal HIGH_MARGIN_THRESHOLD = new BigDecimal("0.15");
    private static final BigDecimal MEDIUM_SCORE_THRESHOLD = new BigDecimal("0.72");
    private static final BigDecimal MEDIUM_MARGIN_THRESHOLD = new BigDecimal("0.08");

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
            ObjectMapper objectMapper) {
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
    }

    public MappingRunResponse execute(GenerateMappingCommand command) {
        if (runRepository.existsByProjectIdAndRelationTypeAndStatus(
                command.projectId(), command.relationType(), MappingRunStatus.RUNNING)) {
            throw AiMappingExceptions.runStillRunning(command.projectId(), command.relationType().name());
        }

        UUID deploymentId = command.modelDeploymentId() != null
                ? command.modelDeploymentId() : properties.getDefaultModelDeploymentId();
        if (deploymentId == null) {
            throw AiMappingExceptions.noDeploymentConfigured();
        }

        ModelDeployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> AiMappingExceptions.noDeploymentConfigured());
        AiModel aiModel = aiModelRepository.findById(deployment.modelId())
                .orElseThrow(() -> AiMappingExceptions.noDeploymentConfigured());
        Provider provider = providerRepository.findById(aiModel.providerId())
                .orElseThrow(() -> AiMappingExceptions.noDeploymentConfigured());
        AiProviderAdapter adapter = adapterRegistry.getAdapter(provider.code().value());

        String promptCode = resolvePromptCode(command.relationType());
        MappingPromptResolverService.ResolvedPrompt resolvedPrompt =
                promptResolverService.resolveByTemplateCode(promptCode);

        MappingRun run = MappingRun.create(
                command.projectId(), command.relationType(),
                command.scope() != null ? command.scope() : MappingScope.UNMAPPED,
                deploymentId, promptCode, properties.getCandidateLimit(), command.requestedBy());
        run = runRepository.save(run);

        List<UnmappedSource> sources;
        try {
            sources = candidateRetrievalService.findUnmappedSources(
                    command.relationType(), command.projectId(), properties.getBatchSize() * 10);
        } catch (Exception e) {
            log.error("Failed to find unmapped sources for run {}: {}", run.id(), e.getMessage(), e);
            run = updateRunStatus(run, MappingRunStatus.FAILED, 0, 0, null);
            return MappingRunResponse.from(run);
        }

        run = updateRunStatus(run, MappingRunStatus.RUNNING, sources.size(), null, null);

        int totalSuggestions = 0;
        int totalInputTokens = 0;
        int totalOutputTokens = 0;

        List<List<UnmappedSource>> batches = partitionIntoBatches(sources, properties.getBatchSize());

        for (List<UnmappedSource> batch : batches) {
            for (UnmappedSource source : batch) {
                try {
                    int count = processSource(source, command, run, adapter, aiModel, provider,
                            resolvedPrompt);
                    totalSuggestions += count;
                } catch (Exception e) {
                    log.warn("Failed to process source {} in run {}: {}", source.id(), run.id(), e.getMessage());
                }
            }
        }

        String tokenUsage = buildTokenUsageJson(totalInputTokens, totalOutputTokens);
        run = updateRunStatus(run, MappingRunStatus.COMPLETED, sources.size(), totalSuggestions, tokenUsage);

        activityLogger.logSuccess(
                AiMappingEntityTypes.MAPPING_RUN, run.id(),
                AiMappingActivityActions.GENERATE_MAPPING_RUN,
                "Generated " + totalSuggestions + " suggestions for " + command.relationType().name());

        return MappingRunResponse.from(run);
    }

    private int processSource(UnmappedSource source, GenerateMappingCommand command,
                               MappingRun run, AiProviderAdapter adapter,
                               AiModel aiModel, Provider provider,
                               MappingPromptResolverService.ResolvedPrompt resolvedPrompt) {
        MappingSummary sourceSummary = summaryBuilderService.getOrBuildSummary(
                source.sourceType(), source.id());
        if (sourceSummary == null) return 0;

        List<CandidateResult> candidateResults = candidateRetrievalService.findCandidates(
                command.relationType(), command.projectId(),
                source.id(), source.searchText(), properties.getCandidateLimit());

        if (candidateResults.isEmpty()) {
            MappingSuggestion noMatch = buildNoMatchSuggestion(source, run);
            suggestionRepository.save(noMatch);
            return 1;
        }

        List<ScoredCandidate> scoredCandidates = new ArrayList<>();
        for (CandidateResult cr : candidateResults) {
            MappingSummary candidateSummary = summaryBuilderService.getOrBuildSummary(
                    cr.targetType(), cr.entityId());
            if (candidateSummary != null) {
                scoredCandidates.add(new ScoredCandidate(candidateSummary, cr.retrievalScore(), cr.rank()));
            }
        }

        if (scoredCandidates.isEmpty()) {
            return 0;
        }

        String inputJson = promptBuilderService.buildInputJson(sourceSummary, scoredCandidates);
        String fullPrompt = renderPrompt(resolvedPrompt, inputJson);

        AiProviderRequest aiRequest = new AiProviderRequest(
                provider.id(), aiModel.providerModelId(), fullPrompt,
                resolvedPrompt.temperature(),
                resolvedPrompt.maxTokens() != null ? resolvedPrompt.maxTokens() : 4096);

        AiProviderResponse aiResponse;
        try {
            aiResponse = adapter.call(aiRequest);
        } catch (Exception e) {
            log.error("AI call failed for source {} in run {}: {}", source.id(), run.id(), e.getMessage());
            throw AiMappingExceptions.aiCallFailed(e.getMessage());
        }

        List<Map<String, Object>> aiResults = parseAiResponse(aiResponse.outputText());
        List<MappingSuggestion> suggestions = buildSuggestions(source, run, scoredCandidates, aiResults);
        suggestionRepository.saveAll(suggestions);
        return suggestions.size();
    }

    private List<Map<String, Object>> parseAiResponse(String outputText) {
        if (outputText == null || outputText.isBlank()) return List.of();
        String json = extractJson(outputText);
        try {
            Object parsed = objectMapper.readValue(json, Object.class);
            if (parsed instanceof List) {
                return objectMapper.convertValue(parsed, new TypeReference<>() {});
            }
            if (parsed instanceof Map<?, ?> map && map.containsKey("suggestions")) {
                return objectMapper.convertValue(map.get("suggestions"), new TypeReference<>() {});
            }
        } catch (Exception e) {
            log.warn("Failed to parse AI response JSON: {}", e.getMessage());
        }
        return List.of();
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

            BigDecimal aiScore = getBigDecimal(result, "aiScore");
            BigDecimal secondBestScore = getBigDecimal(result, "secondBestScore");
            BigDecimal scoreMargin = aiScore != null && secondBestScore != null
                    ? aiScore.subtract(secondBestScore) : null;
            String decisionStr = getString(result, "decision");
            SuggestionDecision decision = decisionStr != null
                    ? safeDecision(decisionStr) : SuggestionDecision.SUGGEST;
            ConfidenceBand confidence = calculateConfidenceBand(aiScore, scoreMargin,
                    getString(result, "warnings"));
            String reasonCodesJson = toJson(result.get("reasonCodes"));
            String evidenceJson = toJson(result.get("evidence"));
            String warningsJson = toJson(result.get("warnings"));

            MappingSuggestion suggestion = new MappingSuggestion(
                    UUID.randomUUID(), run.id(),
                    source.sourceType(), source.id(), source.entityVersion(), null,
                    matched.summary().entityType(), matched.summary().entityId(),
                    matched.summary().entityVersion(), null,
                    run.relationType(), matched.rank(),
                    BigDecimal.valueOf(matched.retrievalScore()), aiScore,
                    aiScore, secondBestScore, scoreMargin,
                    confidence, decision,
                    reasonCodesJson, evidenceJson, warningsJson, null,
                    SuggestionReviewStatus.PENDING, null, null,
                    Instant.now(), Instant.now()
            );
            suggestions.add(suggestion);
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

    private MappingRun updateRunStatus(MappingRun run, MappingRunStatus status,
                                        Integer sourceCount, Integer suggestionCount,
                                        String tokenUsage) {
        MappingRun updated = new MappingRun(
                run.id(), run.projectId(), run.relationType(), run.scope(),
                run.modelDeploymentId(), run.promptKey(), run.promptVersion(), run.summaryVersion(),
                run.candidateLimit(), run.requestedBy(),
                status == MappingRunStatus.RUNNING ? Instant.now() : run.startedAt(),
                status == MappingRunStatus.COMPLETED || status == MappingRunStatus.FAILED ? Instant.now() : run.completedAt(),
                status,
                sourceCount != null ? sourceCount : run.sourceCount(),
                suggestionCount != null ? suggestionCount : run.suggestionCount(),
                tokenUsage != null ? tokenUsage : run.tokenUsageJson(),
                run.createdAt(), run.updatedAt()
        );
        return runRepository.save(updated);
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
            case FUNCTION_TO_USE_CASE -> AiMappingPromptKeys.UC_FUNCTION_PROMPT_CODE;
            case USE_CASE_TO_TEST_CASE -> AiMappingPromptKeys.TC_UC_PROMPT_CODE;
        };
    }

    private static <T> List<List<T>> partitionIntoBatches(List<T> items, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < items.size(); i += batchSize) {
            batches.add(items.subList(i, Math.min(i + batchSize, items.size())));
        }
        return batches;
    }

    private String buildTokenUsageJson(int inputTokens, int outputTokens) {
        return "{\"inputTokens\":" + inputTokens + ",\"outputTokens\":" + outputTokens + "}";
    }

    private String toJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    private static BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return null;
        try {
            return new BigDecimal(v.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private static SuggestionDecision safeDecision(String value) {
        try {
            return SuggestionDecision.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return SuggestionDecision.SUGGEST;
        }
    }
}
