package com.company.scopery.modules.elicitation.question.application.action;

import com.company.scopery.modules.elicitation.question.application.command.AnswerQuestionCommand;
import com.company.scopery.modules.elicitation.question.application.response.ElicitationQuestionResponse;
import com.company.scopery.modules.elicitation.question.domain.enums.QuestionStatus;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestionRepository;
import com.company.scopery.modules.elicitation.round.domain.enums.RoundStatus;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AnswerQuestionAction {

    private final ElicitationQuestionRepository questionRepository;
    private final ElicitationRoundRepository roundRepository;
    private final ElicitationActivityLogger activityLogger;

    public AnswerQuestionAction(ElicitationQuestionRepository questionRepository,
                                 ElicitationRoundRepository roundRepository,
                                 ElicitationActivityLogger activityLogger) {
        this.questionRepository = questionRepository;
        this.roundRepository = roundRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ElicitationQuestionResponse execute(AnswerQuestionCommand command) {
        ElicitationQuestion question = questionRepository.findById(command.questionId())
                .filter(q -> q.sessionId().equals(command.sessionId()))
                .orElseThrow(() -> ElicitationExceptions.questionNotFound(command.questionId()));

        if (question.roundId() != null) {
            roundRepository.findById(question.roundId()).ifPresent(round -> {
                if (round.status() != RoundStatus.ACTIVE) {
                    throw ElicitationExceptions.roundNotActive(round.id());
                }
            });
        }

        if (question.status() == QuestionStatus.ANSWERED) {
            throw ElicitationExceptions.questionAlreadyAnswered(question.id());
        }
        if (question.status() == QuestionStatus.SKIPPED) {
            throw ElicitationExceptions.questionAlreadySkipped(question.id());
        }

        question.answer(command.answerText());
        ElicitationQuestion saved = questionRepository.save(question);

        activityLogger.logSuccess(
                ElicitationEntityTypes.QUESTION,
                saved.id(),
                ElicitationActivityActions.ANSWER_QUESTION,
                "Question answered in session: " + saved.sessionId()
        );

        return ElicitationQuestionResponse.from(saved);
    }
}
