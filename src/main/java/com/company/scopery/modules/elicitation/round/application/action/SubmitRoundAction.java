package com.company.scopery.modules.elicitation.round.application.action;

import com.company.scopery.integration.ai.AiProviderAdapter;
import com.company.scopery.integration.ai.AiProviderAdapterRegistry;
import com.company.scopery.integration.ai.AiProviderRequest;
import com.company.scopery.integration.ai.AiProviderResponse;
import com.company.scopery.modules.elicitation.question.domain.enums.ClarityLevel;
import com.company.scopery.modules.elicitation.question.domain.enums.QuestionStatus;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestionRepository;
import com.company.scopery.modules.elicitation.round.application.command.SubmitRoundCommand;
import com.company.scopery.modules.elicitation.round.application.response.ElicitationRoundResponse;
import com.company.scopery.modules.elicitation.round.application.response.SubmitRoundResponse;
import com.company.scopery.modules.elicitation.round.application.response.SubmitRoundResponse.RoundEvaluation;
import com.company.scopery.modules.elicitation.round.domain.enums.RoundStatus;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SubmitRoundAction {

    private static final Logger log = LoggerFactory.getLogger(SubmitRoundAction.class);
    private static final String TEMPLATE_CODE = "ELICITATION_EVALUATE_ANSWERS_V1";

    private final ElicitationSessionRepository sessionRepository;
    private final ElicitationRoundRepository roundRepository;
    private final ElicitationQuestionRepository questionRepository;
    private final ElicitationScopeLoader scopeLoader;
    private final MappingPromptResolverService promptResolver;
    private final AiProviderAdapterRegistry adapterRegistry;
    private final ElicitationActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public SubmitRoundAction(ElicitationSessionRepository sessionRepository,
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

    public SubmitRoundResponse execute(SubmitRoundCommand command) {
        ElicitationSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(command.sessionId()));

        if (session.status() != SessionStatus.ACTIVE) {
            throw ElicitationExceptions.sessionNotActive(session.id());
        }

        ElicitationRound round = roundRepository.findById(command.roundId())
                .filter(r -> r.sessionId().equals(command.sessionId()))
                .orElseThrow(() -> ElicitationExceptions.roundNotFound(command.roundId()));

        if (round.status() != RoundStatus.ACTIVE) {
            throw ElicitationExceptions.roundNotActive(round.id());
        }

        List<ElicitationQuestion> roundQuestions = questionRepository.findAllByRoundId(round.id());
        boolean hasAnswered = roundQuestions.stream().anyMatch(q -> q.status() == QuestionStatus.ANSWERED);
        if (!hasAnswered) {
            throw ElicitationExceptions.roundNoAnsweredQuestions(round.id());
        }

        List<ElicitationQuestion> allSessionQuestions = questionRepository.findAllBySessionId(session.id());

        ElicitationScopeLoader.ScopeContext scope = scopeLoader.load(session.projectId(), session.scopePackageId());
        String scopeContextJson = buildScopeContextJson(scope);
        String roundQuestionsJson = buildQuestionsJson(roundQuestions);
        String allQaJson = buildAllQaJson(allSessionQuestions);

        MappingPromptResolverService.ResolvedPrompt resolved;
        try {
            resolved = promptResolver.resolveByTemplateCode(TEMPLATE_CODE);
        } catch (Exception e) {
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(),
                    "Prompt template not configured: " + TEMPLATE_CODE);
        }

        String language = session.language() != null ? session.language() : "en";
        String prompt = resolved.userPromptTemplate()
                .replace("{{SCOPE_CONTEXT_JSON}}", scopeContextJson)
                .replace("{{ROUND_QUESTIONS_JSON}}", roundQuestionsJson)
                .replace("{{ALL_QA_JSON}}", allQaJson)
                .replace("{{ROUND_NUMBER}}", String.valueOf(round.roundNumber()))
                .replace("{{QUESTIONS_AND_ANSWERS_JSON}}", roundQuestionsJson)  // backward compat
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
            log.error("AI evaluate-answers failed for round {} in session {}", round.id(), session.id(), e);
            throw ElicitationExceptions.elicitationAiCallFailed(session.id(), e.getMessage());
        }

        ParsedEvaluation parsed = parseEvaluation(session.id(), response.outputText());
        ElicitationRound savedRound = persistEvaluation(round, roundQuestions, parsed);

        activityLogger.logSuccess(
                ElicitationEntityTypes.SESSION,
                session.id(),
                ElicitationActivityActions.SUBMIT_ROUND,
                "Round " + round.roundNumber() + " evaluated: overallClarity=" + parsed.overallClarity()
                        + ", shouldContinue=" + parsed.shouldContinue()
        );

        return new SubmitRoundResponse(
                ElicitationRoundResponse.from(savedRound),
                parsed.evaluations(),
                parsed.shouldContinue(),
                parsed.evaluationSummary()
        );
    }

    @Transactional
    protected ElicitationRound persistEvaluation(ElicitationRound round,
                                                  List<ElicitationQuestion> roundQuestions,
                                                  ParsedEvaluation parsed) {
        Map<UUID, RoundEvaluation> evalById = parsed.evaluations().stream()
                .collect(Collectors.toMap(RoundEvaluation::questionId, Function.identity()));

        for (ElicitationQuestion q : roundQuestions) {
            RoundEvaluation eval = evalById.get(q.id());
            if (eval == null) continue;
            ClarityLevel clarity = parseClarityLevel(eval.clarityLevel());
            q.evaluate(clarity, eval.feedback());
            questionRepository.save(q);
        }

        String questionsJson = buildQuestionsSnapshotJson(roundQuestions, evalById);
        round.markEvaluated(questionsJson, parsed.overallClarity(), parsed.shouldContinue());
        return roundRepository.save(round);
    }

    private ParsedEvaluation parseEvaluation(UUID sessionId, String rawResponse) {
        try {
            String json = extractJson(rawResponse);
            JsonNode root = objectMapper.readTree(json);

            List<RoundEvaluation> evaluations = new ArrayList<>();
            JsonNode evalsNode = root.path("evaluations");
            if (evalsNode.isArray()) {
                for (JsonNode item : evalsNode) {
                    String qIdStr = item.path("questionId").asText(null);
                    if (qIdStr == null) continue;
                    UUID questionId;
                    try {
                        questionId = UUID.fromString(qIdStr);
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                    String clarityStr = item.path("clarityLevel").asText(null);
                    String feedback = nullIfBlank(item.path("feedback").asText(null));
                    String conflictNote = nullIfBlank(item.path("conflictNote").asText(null));
                    evaluations.add(new RoundEvaluation(questionId, clarityStr, feedback, conflictNote));
                }
            }

            String overallClarityStr = root.path("overallClarity").asText(null);
            ClarityLevel overallClarity = parseClarityLevel(overallClarityStr);
            if (overallClarity == null) {
                overallClarity = deriveOverallClarity(evaluations);
            }

            boolean shouldContinue = root.path("shouldContinue").asBoolean(true);
            String evaluationSummary = nullIfBlank(root.path("evaluationSummary").asText(null));

            return new ParsedEvaluation(evaluations, overallClarity, shouldContinue, evaluationSummary);
        } catch (Exception e) {
            log.error("Failed to parse AI evaluation response for session {}", sessionId, e);
            return new ParsedEvaluation(List.of(), ClarityLevel.IMPORTANT, true, null);
        }
    }

    private ClarityLevel deriveOverallClarity(List<RoundEvaluation> evaluations) {
        ClarityLevel worst = ClarityLevel.CLEARED;
        for (RoundEvaluation eval : evaluations) {
            ClarityLevel c = parseClarityLevel(eval.clarityLevel());
            if (c == null) continue;
            if (c.ordinal() < worst.ordinal()) worst = c;
        }
        return worst;
    }

    private ClarityLevel parseClarityLevel(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return ClarityLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
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

    private String buildQuestionsJson(List<ElicitationQuestion> questions) {
        try {
            List<Map<String, Object>> list = questions.stream().map(q -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("questionId", q.id().toString());
                m.put("sequence", q.sequence());
                m.put("questionText", q.questionText());
                m.put("answerText", q.answerText() != null ? q.answerText() : "");
                m.put("status", q.status().name());
                return m;
            }).toList();
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private String buildAllQaJson(List<ElicitationQuestion> allQuestions) {
        try {
            List<Map<String, Object>> list = allQuestions.stream().map(q -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("sequence", q.sequence());
                m.put("questionId", q.id().toString());
                m.put("questionText", q.questionText());
                m.put("answerText", q.answerText());
                m.put("aiFeedback", q.aiFeedback());
                m.put("status", q.status().name());
                m.put("clarityLevel", q.clarityLevel() != null ? q.clarityLevel().name() : null);
                return m;
            }).toList();
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private String buildQuestionsSnapshotJson(List<ElicitationQuestion> questions,
                                               Map<UUID, RoundEvaluation> evalById) {
        try {
            List<Map<String, Object>> list = questions.stream().map(q -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("questionId", q.id().toString());
                m.put("sequence", q.sequence());
                m.put("questionText", q.questionText());
                m.put("answerText", q.answerText());
                m.put("status", q.status().name());
                RoundEvaluation eval = evalById.get(q.id());
                if (eval != null) {
                    m.put("clarityLevel", eval.clarityLevel());
                    m.put("feedback", eval.feedback());
                    m.put("conflictNote", eval.conflictNote());
                }
                return m;
            }).toList();
            return objectMapper.writeValueAsString(list);
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

    private static String nullIfBlank(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private record ParsedEvaluation(
            List<RoundEvaluation> evaluations,
            ClarityLevel overallClarity,
            boolean shouldContinue,
            String evaluationSummary
    ) {}
}
