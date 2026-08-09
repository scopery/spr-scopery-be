package com.company.scopery.modules.elicitation.suggestion.application.action;

import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestionRepository;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.company.scopery.modules.elicitation.shared.util.ElicitationScopeLoader;
import com.company.scopery.modules.elicitation.suggestion.application.command.GenerateSuggestionsCommand;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component("elicitationGenerateSuggestionsAction")
public class GenerateSuggestionsAction {

    private static final Logger log = LoggerFactory.getLogger(GenerateSuggestionsAction.class);
    private static final String TEMPLATE_CODE = "ELICITATION_GENERATE_SUGGESTIONS_V1";

    private final ElicitationRoundRepository roundRepository;
    private final ElicitationSessionRepository sessionRepository;
    private final ElicitationQuestionRepository questionRepository;
    private final ElicitationSuggestionRepository suggestionRepository;
    private final ElicitationScopeLoader scopeLoader;
    private final MappingPromptResolverService promptResolver;
    private final AiProviderAdapterRegistry adapterRegistry;
    private final ElicitationActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public GenerateSuggestionsAction(ElicitationRoundRepository roundRepository,
                                      ElicitationSessionRepository sessionRepository,
                                      ElicitationQuestionRepository questionRepository,
                                      ElicitationSuggestionRepository suggestionRepository,
                                      ElicitationScopeLoader scopeLoader,
                                      MappingPromptResolverService promptResolver,
                                      AiProviderAdapterRegistry adapterRegistry,
                                      ElicitationActivityLogger activityLogger,
                                      ObjectMapper objectMapper) {
        this.roundRepository = roundRepository;
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.suggestionRepository = suggestionRepository;
        this.scopeLoader = scopeLoader;
        this.promptResolver = promptResolver;
        this.adapterRegistry = adapterRegistry;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    public ElicitationSuggestionResponse execute(GenerateSuggestionsCommand command) {
        UUID roundId = command.roundId();
        ElicitationRound round = roundRepository.findById(roundId)
                .orElseThrow(() -> ElicitationExceptions.roundNotFound(roundId));

        ElicitationSession session = sessionRepository.findById(round.sessionId())
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(round.sessionId()));

        ElicitationScopeLoader.ScopeContext scope = scopeLoader.load(session.projectId(), session.scopePackageId());
        String scopeContextJson = buildScopeContextJson(scope);

        List<ElicitationQuestion> allQuestions = questionRepository.findAllBySessionId(session.id());
        String allQaJson = buildAllQaJson(allQuestions);

        MappingPromptResolverService.ResolvedPrompt resolved;
        try {
            resolved = promptResolver.resolveByTemplateCode(TEMPLATE_CODE);
        } catch (Exception e) {
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(),
                    "Prompt template not configured: " + TEMPLATE_CODE);
        }

        List<JsonNode> requirements = parseRequirements(scope.requirementsJson());
        AiProviderAdapter adapter = adapterRegistry.getAdapter(resolved.providerCode());

        List<RequirementItems> allItems = new ArrayList<>();
        List<Map<String, Object>> accumulatedSummaries = new ArrayList<>();
        int globalSeq = 0;

        for (JsonNode req : requirements) {
            UUID requirementId = parseUuid(req.path("id").asText(null));
            if (requirementId == null) continue;

            String requirementJson = req.toString();
            String accumulatedJson = buildAccumulatedJson(accumulatedSummaries);

            String prompt = resolved.userPromptTemplate()
                    .replace("{{SCOPE_CONTEXT_JSON}}", scopeContextJson)
                    .replace("{{ALL_QA_JSON}}", allQaJson)
                    .replace("{{REQUIREMENT_JSON}}", requirementJson)
                    .replace("{{ACCUMULATED_SUGGESTIONS_JSON}}", accumulatedJson)
                    .replace("{{ELICITATION_ROUND_JSON}}", round.questionsJson());  // backward compat

            AiProviderResponse response;
            try {
                response = adapter.call(new AiProviderRequest(
                        null, resolved.modelId(), prompt,
                        resolved.temperature() != null ? resolved.temperature() : BigDecimal.valueOf(0.2),
                        resolved.maxTokens() != null ? resolved.maxTokens() : 8192,
                        resolved.systemPrompt()
                ));
            } catch (Exception e) {
                log.error("AI generate-suggestions failed for requirement {} in round {}", requirementId, roundId, e);
                continue;
            }

            List<ElicitationSuggestionItem> parsed = parseItems(response.outputText(), requirementId,
                    accumulatedJson, globalSeq);
            globalSeq += parsed.size();
            allItems.add(new RequirementItems(requirementId, parsed, extractRequirementSummary(response.outputText())));

            if (!parsed.isEmpty()) {
                Map<String, Object> summary = new LinkedHashMap<>();
                summary.put("requirementId", requirementId.toString());
                summary.put("requirementTitle", req.path("title").asText(""));
                summary.put("suggestedActions", parsed.stream().map(ElicitationSuggestionItem::action).toList());
                accumulatedSummaries.add(summary);
            }
        }

        String overallSummary = buildOverallSummary(allItems);
        List<ElicitationSuggestionItem> flatItems = allItems.stream()
                .flatMap(ri -> ri.items().stream()).toList();

        return persistSuggestion(round, session.id(), overallSummary, flatItems);
    }

    @Transactional
    protected ElicitationSuggestionResponse persistSuggestion(ElicitationRound round, UUID sessionId,
                                                               String overallSummary,
                                                               List<ElicitationSuggestionItem> items) {
        suggestionRepository.deleteWithItemsByRoundId(round.id());
        ElicitationSuggestion suggestion = ElicitationSuggestion.create(round.id(), overallSummary, null);
        ElicitationSuggestion saved = suggestionRepository.save(suggestion);

        List<ElicitationSuggestionItem> itemsWithSuggestionId = items.stream()
                .map(item -> ElicitationSuggestionItem.create(
                        saved.id(), item.sequence(), item.action(),
                        item.targetEntityType(), item.targetEntityId(), item.targetEntityName(),
                        item.rationale(), item.changesJson(), item.preconditionActionsJson(),
                        item.estimatedImpact(), item.requirementId(), item.chainingContextJson()))
                .toList();

        List<ElicitationSuggestionItem> savedItems = suggestionRepository.saveAllItems(itemsWithSuggestionId);

        activityLogger.logSuccess(
                ElicitationEntityTypes.ROUND,
                round.id(),
                ElicitationActivityActions.GENERATE_SUGGESTIONS,
                "Generated " + savedItems.size() + " suggestion items for round " + round.roundNumber()
        );

        return ElicitationSuggestionResponse.from(saved, savedItems);
    }

    private List<JsonNode> parseRequirements(String requirementsJson) {
        try {
            JsonNode arr = objectMapper.readTree(requirementsJson);
            if (!arr.isArray()) return List.of();
            List<JsonNode> list = new ArrayList<>();
            arr.forEach(list::add);
            return list;
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<ElicitationSuggestionItem> parseItems(String rawResponse, UUID requirementId,
                                                        String chainingContextJson, int startSeq) {
        List<ElicitationSuggestionItem> items = new ArrayList<>();
        try {
            String json = extractJson(rawResponse);
            JsonNode root = objectMapper.readTree(json);
            JsonNode suggestionsNode = root.path("suggestions");
            if (!suggestionsNode.isArray()) return items;
            int seq = startSeq;
            for (JsonNode s : suggestionsNode) {
                String action = s.path("action").asText(null);
                if (action == null || action.isBlank()) continue;
                seq++;
                SuggestionItemImpact impact = parseImpact(s.path("estimatedImpact").asText("MEDIUM"));
                String changesJson = s.has("changesJson") ? s.get("changesJson").toString() : null;
                String preconditionsJson = s.has("preconditionActionsJson")
                        ? s.get("preconditionActionsJson").toString() : null;
                UUID targetId = parseUuid(s.path("targetEntityId").asText(null));
                items.add(ElicitationSuggestionItem.create(
                        null, seq, action,
                        s.path("targetEntityType").asText(null),
                        targetId,
                        s.path("targetEntityName").asText(null),
                        s.path("rationale").asText(""),
                        changesJson, preconditionsJson, impact,
                        requirementId, chainingContextJson
                ));
            }
        } catch (Exception e) {
            log.warn("Failed to parse suggestion items for requirement {}: {}", requirementId, e.getMessage());
        }
        return items;
    }

    private String extractRequirementSummary(String rawResponse) {
        try {
            String json = extractJson(rawResponse);
            JsonNode root = objectMapper.readTree(json);
            String summary = root.path("requirementSummary").asText(null);
            return (summary != null && !summary.isBlank()) ? summary : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String buildOverallSummary(List<RequirementItems> allItems) {
        long totalItems = allItems.stream().mapToLong(ri -> ri.items().size()).sum();
        long requirementsWithSuggestions = allItems.stream().filter(ri -> !ri.items().isEmpty()).count();
        return "Generated " + totalItems + " suggestions across " + requirementsWithSuggestions + " requirements.";
    }

    private String buildScopeContextJson(ElicitationScopeLoader.ScopeContext scope) {
        try {
            Map<String, Object> ctx = new LinkedHashMap<>();
            ctx.put("scopeName", scope.scopeName());
            ctx.put("requirements", objectMapper.readValue(scope.requirementsJson(), Object.class));
            ctx.put("functions", objectMapper.readValue(scope.functionsJson(), Object.class));
            ctx.put("useCases", objectMapper.readValue(scope.useCasesJson(), Object.class));
            return objectMapper.writeValueAsString(ctx);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String buildAllQaJson(List<ElicitationQuestion> questions) {
        try {
            List<Map<String, Object>> list = questions.stream().map(q -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("sequence", q.sequence());
                m.put("questionText", q.questionText());
                m.put("answerText", q.answerText());
                m.put("status", q.status().name());
                m.put("clarityLevel", q.clarityLevel() != null ? q.clarityLevel().name() : null);
                return m;
            }).toList();
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private String buildAccumulatedJson(List<Map<String, Object>> summaries) {
        try {
            return summaries.isEmpty() ? "[]" : objectMapper.writeValueAsString(summaries);
        } catch (Exception e) {
            return "[]";
        }
    }

    private static String extractJson(String text) {
        if (text == null) return "{}";
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        return text;
    }

    private static UUID parseUuid(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private SuggestionItemImpact parseImpact(String value) {
        try {
            return SuggestionItemImpact.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return SuggestionItemImpact.MEDIUM;
        }
    }

    private record RequirementItems(UUID requirementId, List<ElicitationSuggestionItem> items, String summary) {}
}
