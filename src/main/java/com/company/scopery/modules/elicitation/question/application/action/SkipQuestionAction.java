package com.company.scopery.modules.elicitation.question.application.action;

import com.company.scopery.modules.elicitation.question.application.command.SkipQuestionCommand;
import com.company.scopery.modules.elicitation.question.application.response.ElicitationQuestionResponse;
import com.company.scopery.modules.elicitation.question.domain.enums.QuestionStatus;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestionRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SkipQuestionAction {

    private final ElicitationQuestionRepository questionRepository;
    private final ElicitationActivityLogger activityLogger;

    public SkipQuestionAction(ElicitationQuestionRepository questionRepository,
                               ElicitationActivityLogger activityLogger) {
        this.questionRepository = questionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ElicitationQuestionResponse execute(SkipQuestionCommand command) {
        ElicitationQuestion question = questionRepository.findById(command.questionId())
                .filter(q -> q.sessionId().equals(command.sessionId()))
                .orElseThrow(() -> ElicitationExceptions.questionNotFound(command.questionId()));

        if (question.status() == QuestionStatus.ANSWERED) {
            throw ElicitationExceptions.questionAlreadyAnswered(question.id());
        }
        if (question.status() == QuestionStatus.SKIPPED) {
            throw ElicitationExceptions.questionAlreadySkipped(question.id());
        }

        question.skip();
        ElicitationQuestion saved = questionRepository.save(question);

        activityLogger.logSuccess(
                ElicitationEntityTypes.QUESTION,
                saved.id(),
                ElicitationActivityActions.SKIP_QUESTION,
                "Question skipped in session: " + saved.sessionId()
        );

        return ElicitationQuestionResponse.from(saved);
    }
}
