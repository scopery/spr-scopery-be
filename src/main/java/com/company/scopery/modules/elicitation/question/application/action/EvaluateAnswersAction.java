package com.company.scopery.modules.elicitation.question.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.elicitation.question.application.command.EvaluateAnswersCommand;
import com.company.scopery.modules.elicitation.question.application.response.ElicitationQuestionResponse;
import com.company.scopery.modules.elicitation.question.domain.enums.ClarityLevel;
import com.company.scopery.modules.elicitation.question.domain.enums.QuestionStatus;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestionRepository;
import com.company.scopery.modules.elicitation.session.domain.enums.SessionStatus;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.company.scopery.modules.elicitation.shared.util.ElicitationScopeLoader;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EvaluateAnswersAction {

    private static final Logger log = LoggerFactory.getLogger(EvaluateAnswersAction.class);
    private static final String TEMPLATE_CODE = "ELICITATION_EVALUATE_ANSWERS_V1";
    private static final String PROVIDER_CODE = "ANTHROPIC";
    private static final String MODEL_ID = "claude-opus-4-5";

    private final ElicitationSessionRepository sessionRepository;
    private final ElicitationQuestionRepository questionRepository;
    private final ElicitationScopeLoader scopeLoader;
    private final MappingPromptResolverService promptResolver;
    private final AiProviderAdapterRegistry adapterRegistry;
    private final ElicitationActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public EvaluateAnswersAction(ElicitationSessionRepository sessionRepository,
                                  ElicitationQuestionRepository questionRepository,
                                  ElicitationScopeLoader scopeLoader,
                                  MappingPromptResolverService promptResolver,
                                  AiProviderAdapterRegistry adapterRegistry,
                                  ElicitationActivityLogger activityLogger,
                                  ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.scopeLoader = scopeLoader;
        this.promptResolver = promptResolver;
        this.adapterRegistry = adapterRegistry;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    public List<ElicitationQuestionResponse> execute(EvaluateAnswersCommand command) {
        ElicitationSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(command.sessionId()));

        if (session.status() != SessionStatus.ACTIVE) {
            throw ElicitationExceptions.sessionNotActive(session.id());
        }

        List<ElicitationQuestion> answeredQuestions = questionRepository.findAllBySessionId(session.id())
                .stream()
                .filter(q -> q.status() == QuestionStatus.ANSWERED)
                .toList();

        if (answeredQuestions.isEmpty()) {
            return List.of();
        }

        ElicitationScopeLoader.ScopeContext scope = scopeLoader.load(session.projectId(), session.scopePackageId());
        String scopeContextJson = buildScopeContextJson(scope);
        String questionsAndAnswersJson = buildQuestionsAndAnswersJson(answeredQuestions);

        MappingPromptResolverService.ResolvedPrompt resolved;
        try {
            resolved = promptResolver.resolveByTemplateCode(TEMPLATE_CODE);
        } catch (Exception e) {
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(), "Prompt template not configured: " + TEMPLATE_CODE);
        }

        String prompt = resolved.userPromptTemplate()
                .replace("{{SCOPE_CONTEXT_JSON}}", scopeContextJson)
                .replace("{{QUESTIONS_AND_ANSWERS_JSON}}", questionsAndAnswersJson);

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
            log.error("Anthropic evaluate-answers failed for session {}", session.id(), e);
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(), e.getMessage());
        }

        List<ElicitationQuestion> updated = applyEvaluationsAndSave(session.id(), answeredQuestions, response.outputText());

        activityLogger.logSuccess(
                ElicitationEntityTypes.SESSION,
                session.id(),
                ElicitationActivityActions.EVALUATE_ANSWERS,
                "Evaluated " + updated.size() + " answers"
        );

        return updated.stream().map(ElicitationQuestionResponse::from).toList();
    }

    @Transactional
    protected List<ElicitationQuestion> applyEvaluationsAndSave(UUID sessionId,
                                                                  List<ElicitationQuestion> answeredQuestions,
                                                                  String rawResponse) {
        Map<UUID, ElicitationQuestion> byId = answeredQuestions.stream()
                .collect(Collectors.toMap(ElicitationQuestion::id, Function.identity()));

        List<ElicitationQuestion> updated = new ArrayList<>();
        List<ElicitationQuestion> feedbackQuestions = new ArrayList<>();
        int maxSeq = questionRepository.findMaxSequenceBySessionId(sessionId);

        try {
            String json = extractJson(rawResponse);
            JsonNode root = objectMapper.readTree(json);
            JsonNode evaluations = root.path("evaluations");
            if (!evaluations.isArray()) {
                throw ElicitationExceptions.elicitationAiResponseInvalid(sessionId, "Expected 'evaluations' array");
            }
            for (JsonNode eval : evaluations) {
                UUID questionId = UUID.fromString(eval.path("questionId").asText());
                String clarityStr = eval.path("clarityLevel").asText(null);
                String feedback = eval.path("feedback").asText(null);

                ElicitationQuestion question = byId.get(questionId);
                if (question == null) continue;

                ClarityLevel clarity = clarityStr != null ? parseClarityLevel(clarityStr) : null;
                question.evaluate(clarity, feedback);
                updated.add(questionRepository.save(question));

                if (feedback != null && !feedback.isBlank() && clarity != ClarityLevel.CLEARED) {
                    maxSeq++;
                    ElicitationQuestion followUp = ElicitationQuestion.createFeedback(
                            sessionId, maxSeq, feedback, questionId);
                    feedbackQuestions.add(followUp);
                }
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw ElicitationExceptions.elicitationAiResponseInvalid(sessionId, e.getMessage());
        }

        if (!feedbackQuestions.isEmpty()) {
            questionRepository.saveAll(feedbackQuestions);
        }

        return updated;
    }

    private static String extractJson(String text) {
        if (text == null) return "{}";
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        return text;
    }

    private ClarityLevel parseClarityLevel(String value) {
        try {
            return ClarityLevel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
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

    private String buildQuestionsAndAnswersJson(List<ElicitationQuestion> questions) {
        try {
            return objectMapper.writeValueAsString(questions.stream().map(q -> Map.of(
                    "questionId", q.id().toString(),
                    "questionText", q.questionText(),
                    "answerText", q.answerText() != null ? q.answerText() : ""
            )).toList());
        } catch (Exception e) {
            return "[]";
        }
    }
}
