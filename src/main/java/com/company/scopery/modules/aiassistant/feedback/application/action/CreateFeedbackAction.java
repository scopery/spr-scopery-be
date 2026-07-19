package com.company.scopery.modules.aiassistant.feedback.application.action;

import com.company.scopery.modules.aiassistant.domain.model.AiAnswerFeedback;
import com.company.scopery.modules.aiassistant.domain.model.AiAnswerFeedbackRepository;
import com.company.scopery.modules.aiassistant.feedback.application.command.CreateFeedbackCommand;
import com.company.scopery.modules.aiassistant.feedback.application.response.AiAnswerFeedbackResponse;
import com.company.scopery.modules.aiassistant.shared.activity.AiAssistantActivityLogger;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantActivityActions;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateFeedbackAction {

    private final AiAnswerFeedbackRepository feedbackRepository;
    private final AiAssistantActivityLogger activityLogger;

    public CreateFeedbackAction(AiAnswerFeedbackRepository feedbackRepository,
                                AiAssistantActivityLogger activityLogger) {
        this.feedbackRepository = feedbackRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiAnswerFeedbackResponse execute(CreateFeedbackCommand cmd) {
        AiAnswerFeedback fb = AiAnswerFeedback.create(
                cmd.conversationId(),
                cmd.messageId(),
                cmd.actorId(),
                cmd.rating(),
                cmd.reasonCode(),
                cmd.comment()
        );
        AiAnswerFeedback saved = feedbackRepository.save(fb);
        activityLogger.logSuccess(
                AiAssistantEntityTypes.AI_ANSWER_FEEDBACK,
                saved.id(),
                AiAssistantActivityActions.CREATE_AI_FEEDBACK,
                "Feedback submitted"
        );
        return AiAnswerFeedbackResponse.from(saved);
    }
}
