package com.company.scopery.modules.elicitation.question.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.elicitation.question.application.command.GenerateQuestionsCommand;
import com.company.scopery.modules.elicitation.question.application.response.ElicitationQuestionResponse;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestionRepository;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.company.scopery.modules.elicitation.shared.util.ElicitationScopeLoader;
import com.company.scopery.modules.elicitation.shared.util.ElicitationScopeLoader.ScopeContext;
import com.company.scopery.modules.elicitation.session.domain.enums.SessionStatus;
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

@Component
public class GenerateQuestionsAction {

    private static final Logger log = LoggerFactory.getLogger(GenerateQuestionsAction.class);
    private static final String TEMPLATE_CODE = "ELICITATION_GENERATE_QUESTIONS_V1";
    private static final String PROVIDER_CODE = "ANTHROPIC";
    private static final String MODEL_ID = "claude-opus-4-5";

    private final ElicitationSessionRepository sessionRepository;
    private final ElicitationQuestionRepository questionRepository;
    private final ElicitationScopeLoader scopeLoader;
    private final MappingPromptResolverService promptResolver;
    private final AiProviderAdapterRegistry adapterRegistry;
    private final ElicitationActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public GenerateQuestionsAction(ElicitationSessionRepository sessionRepository,
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

    public List<ElicitationQuestionResponse> execute(GenerateQuestionsCommand command) {
        ElicitationSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(command.sessionId()));

        if (session.status() != SessionStatus.ACTIVE) {
            throw ElicitationExceptions.sessionNotActive(session.id());
        }

        ScopeContext scope = scopeLoader.load(session.projectId(), session.scopePackageId());

        MappingPromptResolverService.ResolvedPrompt resolved;
        try {
            resolved = promptResolver.resolveByTemplateCode(TEMPLATE_CODE);
        } catch (Exception e) {
            log.warn("Prompt template {} not found, using empty fallback", TEMPLATE_CODE);
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(), "Prompt template not configured: " + TEMPLATE_CODE);
        }

        String prompt = resolved.userPromptTemplate()
                .replace("{{SCOPE_NAME}}", scope.scopeName())
                .replace("{{REQUIREMENTS_JSON}}", scope.requirementsJson())
                .replace("{{FUNCTIONS_JSON}}", scope.functionsJson())
                .replace("{{USE_CASES_JSON}}", scope.useCasesJson())
                .replace("{{SCREENS_JSON}}", scope.screensJson())
                .replace("{{APIS_JSON}}", scope.apisJson())
                .replace("{{ENTITIES_JSON}}", scope.entitiesJson())
                .replace("{{COMPONENTS_JSON}}", scope.componentsJson());

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
            log.error("Anthropic call failed for session {}", session.id(), e);
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(), e.getMessage());
        }

        List<ElicitationQuestion> questions = parseQuestions(session.id(), response.outputText(), questionRepository.findMaxSequenceBySessionId(session.id()));

        List<ElicitationQuestion> saved = savingInTransaction(questions);

        activityLogger.logSuccess(
                ElicitationEntityTypes.SESSION,
                session.id(),
                ElicitationActivityActions.GENERATE_QUESTIONS,
                "Generated " + saved.size() + " elicitation questions"
        );

        return saved.stream().map(ElicitationQuestionResponse::from).toList();
    }

    @Transactional
    protected List<ElicitationQuestion> savingInTransaction(List<ElicitationQuestion> questions) {
        return questionRepository.saveAll(questions);
    }

    private static String extractJson(String text) {
        if (text == null) return "{}";
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) return text.substring(start, end + 1);
        return text;
    }

    private List<ElicitationQuestion> parseQuestions(java.util.UUID sessionId, String rawResponse, int existingMaxSeq) {
        List<ElicitationQuestion> list = new ArrayList<>();
        try {
            String json = extractJson(rawResponse);
            JsonNode root = objectMapper.readTree(json);
            JsonNode questionsNode = root.path("questions");
            if (!questionsNode.isArray()) {
                throw ElicitationExceptions.elicitationAiResponseInvalid(sessionId, "Expected 'questions' array in AI response");
            }
            int seq = existingMaxSeq;
            for (JsonNode item : questionsNode) {
                String text = item.path("questionText").asText(null);
                if (text == null || text.isBlank()) continue;
                seq++;
                list.add(ElicitationQuestion.createAiGenerated(sessionId, seq, text));
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw ElicitationExceptions.elicitationAiResponseInvalid(sessionId, e.getMessage());
        }
        return list;
    }
}
