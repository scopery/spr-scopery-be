package com.company.scopery.modules.elicitation.session.application.action;

import com.company.scopery.modules.elicitation.question.domain.enums.ClarityLevel;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestionRepository;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
import com.company.scopery.modules.elicitation.session.application.command.CloseSessionCommand;
import com.company.scopery.modules.elicitation.session.application.response.ElicitationSessionResponse;
import com.company.scopery.modules.elicitation.session.domain.enums.SessionStatus;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class CloseElicitationSessionAction {

    private final ElicitationSessionRepository sessionRepository;
    private final ElicitationQuestionRepository questionRepository;
    private final ElicitationRoundRepository roundRepository;
    private final ElicitationActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public CloseElicitationSessionAction(ElicitationSessionRepository sessionRepository,
                                          ElicitationQuestionRepository questionRepository,
                                          ElicitationRoundRepository roundRepository,
                                          ElicitationActivityLogger activityLogger,
                                          ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.roundRepository = roundRepository;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ElicitationSessionResponse execute(CloseSessionCommand command) {
        ElicitationSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(command.sessionId()));

        if (session.status() == SessionStatus.CLOSED) {
            throw ElicitationExceptions.sessionAlreadyClosed(session.id());
        }
        if (session.status() == SessionStatus.CANCELLED) {
            throw ElicitationExceptions.sessionAlreadyCancelled(session.id());
        }

        List<ElicitationQuestion> questions = questionRepository.findAllBySessionId(session.id());
        boolean hasAnswered = questions.stream()
                .anyMatch(q -> q.status().name().equals("ANSWERED"));
        if (!hasAnswered) {
            throw ElicitationExceptions.sessionNoAnsweredQuestions(session.id());
        }

        ClarityLevel overallClarity = computeOverallClarity(questions);
        String questionsJson = serializeQuestions(questions);

        int roundNumber = roundRepository.countBySessionId(session.id()) + 1;
        ElicitationRound round = ElicitationRound.create(session.id(), roundNumber, questionsJson, overallClarity);
        roundRepository.save(round);

        session.close();
        ElicitationSession saved = sessionRepository.save(session);

        activityLogger.logSuccess(
                ElicitationEntityTypes.SESSION,
                saved.id(),
                ElicitationActivityActions.CLOSE_SESSION,
                "Elicitation session closed, round " + roundNumber + " created"
        );

        return ElicitationSessionResponse.from(saved);
    }

    private ClarityLevel computeOverallClarity(List<ElicitationQuestion> questions) {
        List<ClarityLevel> orderedByWorst = List.of(
                ClarityLevel.BLOCKED, ClarityLevel.CRITICAL, ClarityLevel.IMPORTANT,
                ClarityLevel.MINOR, ClarityLevel.CLEARED);
        Optional<ClarityLevel> worst = questions.stream()
                .filter(q -> q.clarityLevel() != null)
                .map(ElicitationQuestion::clarityLevel)
                .min(Comparator.comparingInt(orderedByWorst::indexOf));
        return worst.orElse(ClarityLevel.CLEARED);
    }

    private String serializeQuestions(List<ElicitationQuestion> questions) {
        try {
            return objectMapper.writeValueAsString(questions.stream().map(q -> {
                java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                map.put("id", q.id());
                map.put("sequence", q.sequence());
                map.put("questionText", q.questionText());
                map.put("answerText", q.answerText());
                map.put("clarityLevel", q.clarityLevel() != null ? q.clarityLevel().name() : null);
                map.put("aiFeedback", q.aiFeedback());
                map.put("status", q.status().name());
                map.put("source", q.source().name());
                return map;
            }).toList());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
