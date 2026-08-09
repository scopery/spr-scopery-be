package com.company.scopery.modules.elicitation.suggestion.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.company.scopery.modules.elicitation.shared.util.ElicitationScopeLoader;
import com.company.scopery.modules.elicitation.suggestion.application.response.ElicitationSuggestionResponse;
import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionItemImpact;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestion;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionRepository;
import com.company.scopery.modules.traceability.aimapping.application.internal.MappingPromptResolverService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GenerateSuggestionsAction {

    private static final Logger log = LoggerFactory.getLogger(GenerateSuggestionsAction.class);
    private static final String TEMPLATE_CODE = "ELICITATION_GENERATE_SUGGESTIONS_V1";
    private static final String PROVIDER_CODE = "ANTHROPIC";
    private static final String MODEL_ID = "claude-opus-4-5";

    private final ElicitationRoundRepository roundRepository;
    private final ElicitationSessionRepository sessionRepository;
    private final ElicitationSuggestionRepository suggestionRepository;
    private final ElicitationScopeLoader scopeLoader;
    private final MappingPromptResolverService promptResolver;
    private final AiProviderAdapterRegistry adapterRegistry;
    private final ElicitationActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public GenerateSuggestionsAction(ElicitationRoundRepository roundRepository,
                                      ElicitationSessionRepository sessionRepository,
                                      ElicitationSuggestionRepository suggestionRepository,
                                      ElicitationScopeLoader scopeLoader,
                                      MappingPromptResolverService promptResolver,
                                      AiProviderAdapterRegistry adapterRegistry,
                                      ElicitationActivityLogger activityLogger,
                                      ObjectMapper objectMapper) {
        this.roundRepository = roundRepository;
        this.sessionRepository = sessionRepository;
        this.suggestionRepository = suggestionRepository;
        this.scopeLoader = scopeLoader;
        this.promptResolver = promptResolver;
        this.adapterRegistry = adapterRegistry;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    public ElicitationSuggestionResponse execute(UUID roundId) {
        ElicitationRound round = roundRepository.findById(roundId)
                .orElseThrow(() -> ElicitationExceptions.roundNotFound(roundId));

        ElicitationSession session = sessionRepository.findById(round.sessionId())
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(round.sessionId()));

        ElicitationScopeLoader.ScopeContext scope = scopeLoader.load(session.projectId(), session.scopePackageId());
        String scopeContextJson = buildScopeContextJson(scope);

        MappingPromptResolverService.ResolvedPrompt resolved;
        try {
            resolved = promptResolver.resolveByTemplateCode(TEMPLATE_CODE);
        } catch (Exception e) {
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(), "Prompt template not configured: " + TEMPLATE_CODE);
        }

        String prompt = resolved.userPromptTemplate()
                .replace("{{SCOPE_CONTEXT_JSON}}", scopeContextJson)
                .replace("{{ELICITATION_ROUND_JSON}}", round.questionsJson());

        AiProviderAdapter adapter = adapterRegistry.getAdapter(PROVIDER_CODE);
        AiProviderResponse response;
        try {
            response = adapter.call(new AiProviderRequest(
                    null, MODEL_ID, prompt,
                    BigDecimal.valueOf(0.2),
                    resolved.maxTokens() != null ? resolved.maxTokens() : 8192,
                    resolved.systemPrompt()
            ));
        } catch (Exception e) {
            log.error("Anthropic generate-suggestions failed for round {}", roundId, e);
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(), e.getMessage());
        }

        return persistSuggestion(round, session.id(), response.outputText());
    }

    @Transactional
    protected ElicitationSuggestionResponse persistSuggestion(ElicitationRound round, UUID sessionId, String rawResponse) {
        String overallSummary = null;
        List<ElicitationSuggestionItem> items = new ArrayList<>();

        try {
            String json = extractJson(rawResponse);
            JsonNode root = objectMapper.readTree(json);
            overallSummary = root.path("overallSummary").asText(null);
            JsonNode suggestionsNode = root.path("suggestions");
            if (suggestionsNode.isArray()) {
                int seq = 0;
                for (JsonNode s : suggestionsNode) {
                    seq++;
                    String action = s.path("action").asText(null);
                    if (action == null || action.isBlank()) continue;
                    String impactStr = s.path("estimatedImpact").asText("MEDIUM");
                    SuggestionItemImpact impact = parseImpact(impactStr);
                    String changesJson = s.has("changes") ? s.get("changes").toString() : null;
                    String preconditionsJson = s.has("preconditions") ? s.get("preconditions").toString() : null;
                    UUID targetId = null;
                    String targetIdStr = s.path("targetEntityId").asText(null);
                    if (targetIdStr != null && !targetIdStr.isBlank()) {
                        try { targetId = UUID.fromString(targetIdStr); } catch (Exception ignored) {}
                    }
                    items.add(ElicitationSuggestionItem.create(
                            null, seq, action,
                            s.path("targetEntityType").asText(null),
                            targetId,
                            s.path("targetEntityName").asText(null),
                            s.path("rationale").asText(""),
                            changesJson, preconditionsJson, impact
                    ));
                }
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw ElicitationExceptions.elicitationAiResponseInvalid(sessionId, e.getMessage());
        }

        ElicitationSuggestion suggestion = ElicitationSuggestion.create(round.id(), overallSummary, rawResponse);
        ElicitationSuggestion saved = suggestionRepository.save(suggestion);

        List<ElicitationSuggestionItem> itemsWithSuggestionId = items.stream()
                .map(item -> ElicitationSuggestionItem.create(
                        saved.id(), item.sequence(), item.action(),
                        item.targetEntityType(), item.targetEntityId(), item.targetEntityName(),
                        item.rationale(), item.changesJson(), item.preconditionActionsJson(),
                        item.estimatedImpact()))
                .toList();

        List<ElicitationSuggestionItem> savedItems = suggestionRepository.saveAllItems(itemsWithSuggestionId);

        round.markCompleted();
        roundRepository.save(round);

        activityLogger.logSuccess(
                ElicitationEntityTypes.ROUND,
                round.id(),
                ElicitationActivityActions.GENERATE_SUGGESTIONS,
                "Generated " + savedItems.size() + " suggestion items for round " + round.roundNumber()
        );

        return ElicitationSuggestionResponse.from(saved, savedItems);
    }

    private static String extractJson(String text) {
        if (text == null) return "{}";
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        return text;
    }

    private SuggestionItemImpact parseImpact(String value) {
        try {
            return SuggestionItemImpact.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return SuggestionItemImpact.MEDIUM;
        }
    }

    private String buildScopeContextJson(ElicitationScopeLoader.ScopeContext scope) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "scopeName", scope.scopeName(),
                    "requirements", objectMapper.readTree(scope.requirementsJson()),
                    "functions", objectMapper.readTree(scope.functionsJson()),
                    "useCases", objectMapper.readTree(scope.useCasesJson())
            ));
        } catch (Exception e) {
            return "{}";
        }
    }
}
