package com.company.scopery.modules.elicitation.round.application.action;

import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestionRepository;
import com.company.scopery.modules.elicitation.question.application.response.ElicitationQuestionResponse;
import com.company.scopery.modules.elicitation.round.application.command.GenerateRoundCommand;
import com.company.scopery.modules.elicitation.round.application.response.ElicitationRoundResponse;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.company.scopery.modules.elicitation.shared.util.ElicitationScopeLoader;
import com.company.scopery.modules.elicitation.session.domain.enums.SessionStatus;
import com.company.scopery.modules.traceability.aimapping.application.internal.MappingPromptResolverService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.function.Consumer;

@Component
public class GenerateRoundAction {

    private static final Logger log = LoggerFactory.getLogger(GenerateRoundAction.class);
    private static final String TEMPLATE_CODE = "ELICITATION_GENERATE_QUESTIONS_V1";
    private static final int QUESTIONS_PER_ROUND = 5;

    private final ElicitationSessionRepository sessionRepository;
    private final ElicitationRoundRepository roundRepository;
    private final ElicitationQuestionRepository questionRepository;
    private final ElicitationScopeLoader scopeLoader;
    private final MappingPromptResolverService promptResolver;
    private final AiProviderAdapterRegistry adapterRegistry;
    private final ElicitationActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public GenerateRoundAction(ElicitationSessionRepository sessionRepository,
                                ElicitationRoundRepository roundRepository,
                                ElicitationQuestionRepository questionRepository,
                                ElicitationScopeLoader scopeLoader,
                                MappingPromptResolverService promptResolver,
                                AiProviderAdapterRegistry adapterRegistry,
                                ElicitationActivityLogger activityLogger,
                                ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.roundRepository = roundRepository;
        this.questionRepository = questionRepository;
        this.scopeLoader = scopeLoader;
        this.promptResolver = promptResolver;
        this.adapterRegistry = adapterRegistry;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    public void executeStreaming(GenerateRoundCommand command,
                                  Consumer<ElicitationQuestionResponse> questionSink,
                                  Consumer<ElicitationRoundResponse> roundSink) {
        ElicitationSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(command.sessionId()));

        if (session.status() != SessionStatus.ACTIVE) {
            throw ElicitationExceptions.sessionNotActive(session.id());
        }

        roundRepository.findActiveBySessionId(session.id()).ifPresent(existing -> {
            throw ElicitationExceptions.roundAlreadyActive(session.id());
        });

        ElicitationScopeLoader.ScopeContext scope = scopeLoader.load(session.projectId(), session.scopePackageId());
        String scopeSnapshotJson = buildSnapshotJson(scope);

        List<ElicitationQuestion> allPreviousQuestions = questionRepository.findAllBySessionId(session.id());
        int globalMaxSeq = allPreviousQuestions.stream().mapToInt(ElicitationQuestion::sequence).max().orElse(0);
        String allQaJson = buildAllQaJson(allPreviousQuestions);

        MappingPromptResolverService.ResolvedPrompt resolved;
        try {
            resolved = promptResolver.resolveByTemplateCode(TEMPLATE_CODE);
        } catch (Exception e) {
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(), "Prompt template not configured: " + TEMPLATE_CODE);
        }

        String language = session.language() != null ? session.language() : "en";
        String prompt = resolved.userPromptTemplate()
                .replace("{{SCOPE_NAME}}", scope.scopeName())
                .replace("{{REQUIREMENTS_JSON}}", scope.requirementsJson())
                .replace("{{FUNCTIONS_JSON}}", scope.functionsJson())
                .replace("{{USE_CASES_JSON}}", scope.useCasesJson())
                .replace("{{SCREENS_JSON}}", scope.screensJson())
                .replace("{{APIS_JSON}}", scope.apisJson())
                .replace("{{NFR_JSON}}", scope.nfrJson())
                .replace("{{ENTITIES_JSON}}", scope.entitiesJson())
                .replace("{{COMPONENTS_JSON}}", scope.componentsJson())
                .replace("{{ALL_QA_JSON}}", allQaJson)
                .replace("{{ROUND_START_SEQ}}", String.valueOf(globalMaxSeq + 1))
                .replace("{{LANGUAGE}}", language);

        AiProviderAdapter adapter = adapterRegistry.getAdapter(resolved.providerCode());
        AiProviderResponse response;
        try {
            response = adapter.call(new AiProviderRequest(
                    null, resolved.modelId(), prompt,
                    resolved.temperature() != null ? resolved.temperature() : BigDecimal.valueOf(0.2),
                    resolved.maxTokens() != null ? resolved.maxTokens() : 8192,
                    resolved.systemPrompt()
            ));
        } catch (Exception e) {
            log.error("AI call failed for session {}", session.id(), e);
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(), e.getMessage());
        }

        int roundNumber = roundRepository.countBySessionId(session.id()) + 1;
        List<ElicitationQuestion> questions = parseQuestions(session.id(), response.outputText(), globalMaxSeq);

        ElicitationRound savedRound = saveRoundAndQuestions(session.id(), roundNumber, scopeSnapshotJson, questions, questionSink);

        activityLogger.logSuccess(
                ElicitationEntityTypes.SESSION,
                session.id(),
                ElicitationActivityActions.GENERATE_QUESTIONS,
                "Generated round " + roundNumber + " with " + questions.size() + " questions"
        );

        roundSink.accept(ElicitationRoundResponse.from(savedRound));
    }

    @Transactional
    protected ElicitationRound saveRoundAndQuestions(UUID sessionId, int roundNumber, String scopeSnapshotJson,
                                                      List<ElicitationQuestion> questions,
                                                      Consumer<ElicitationQuestionResponse> questionSink) {
        ElicitationRound round = ElicitationRound.create(sessionId, roundNumber, scopeSnapshotJson);
        ElicitationRound savedRound = roundRepository.save(round);

        for (ElicitationQuestion q : questions) {
            ElicitationQuestion withRoundId = ElicitationQuestion.createAiGenerated(
                    q.sessionId(), savedRound.id(), q.sequence(), q.questionText(), q.suggestedAnswers());
            ElicitationQuestion saved = questionRepository.save(withRoundId);
            questionSink.accept(ElicitationQuestionResponse.from(saved));
        }

        return savedRound;
    }

    private List<ElicitationQuestion> parseQuestions(UUID sessionId, String rawResponse, int existingMaxSeq) {
        List<ElicitationQuestion> list = new ArrayList<>();
        try {
            String json = extractJson(rawResponse);
            JsonNode root = objectMapper.readTree(json);
            JsonNode questionsNode = root.path("questions");
            if (!questionsNode.isArray()) return list;
            int seq = existingMaxSeq;
            int count = 0;
            for (JsonNode item : questionsNode) {
                if (count >= QUESTIONS_PER_ROUND) break;
                String text = item.path("questionText").asText(null);
                if (text == null || text.isBlank()) continue;
                seq++;
                count++;
                List<String> suggestedAnswers = new ArrayList<>();
                JsonNode answersNode = item.path("suggestedAnswers");
                if (answersNode.isArray()) {
                    for (JsonNode ans : answersNode) {
                        String val = ans.asText(null);
                        if (val != null && !val.isBlank()) suggestedAnswers.add(val);
                    }
                }
                // roundId is null here — will be set in saveRoundAndQuestions after round is persisted
                list.add(ElicitationQuestion.createAiGenerated(sessionId, null, seq, text,
                        suggestedAnswers.isEmpty() ? null : suggestedAnswers));
            }
        } catch (Exception e) {
            log.error("Failed to parse AI question response for session {}", sessionId, e);
        }
        return list;
    }

    private String buildSnapshotJson(ElicitationScopeLoader.ScopeContext scope) {
        try {
            Map<String, Object> snapshot = new LinkedHashMap<>();
            snapshot.put("scopeName", scope.scopeName());
            snapshot.put("requirements", objectMapper.readValue(scope.requirementsJson(), Object.class));
            snapshot.put("functions", objectMapper.readValue(scope.functionsJson(), Object.class));
            snapshot.put("useCases", objectMapper.readValue(scope.useCasesJson(), Object.class));
            snapshot.put("screens", objectMapper.readValue(scope.screensJson(), Object.class));
            snapshot.put("nfr", objectMapper.readValue(scope.nfrJson(), Object.class));
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private String buildAllQaJson(List<ElicitationQuestion> questions) {
        try {
            List<Map<String, Object>> qaList = questions.stream().map(q -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("sequence", q.sequence());
                m.put("questionText", q.questionText());
                m.put("answerText", q.answerText());
                m.put("aiFeedback", q.aiFeedback());
                m.put("status", q.status().name());
                m.put("clarityLevel", q.clarityLevel() != null ? q.clarityLevel().name() : null);
                return m;
            }).toList();
            return objectMapper.writeValueAsString(qaList);
        } catch (JsonProcessingException e) {
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
}
