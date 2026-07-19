package com.company.scopery.modules.aiassistant.message.application.action;

import com.company.scopery.modules.aiassistant.domain.model.AiMessage;
import com.company.scopery.modules.aiassistant.domain.model.AiMessageRepository;
import com.company.scopery.modules.aiassistant.message.application.command.CancelMessageCommand;
import com.company.scopery.modules.aiassistant.shared.activity.AiAssistantActivityLogger;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantActivityActions;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantEntityTypes;
import com.company.scopery.modules.aiassistant.shared.error.AiAssistantExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CancelMessageAction {

    private final AiMessageRepository messageRepository;
    private final AiAssistantActivityLogger activityLogger;

    public CancelMessageAction(AiMessageRepository messageRepository,
                               AiAssistantActivityLogger activityLogger) {
        this.messageRepository = messageRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(CancelMessageCommand cmd) {
        AiMessage msg = messageRepository
                .findByIdAndConversationId(cmd.messageId(), cmd.conversationId())
                .orElseThrow(() -> AiAssistantExceptions.messageNotFound(cmd.messageId()));
        if (!msg.isFinal()) {
            msg.markCancelRequested(cmd.actorId());
        }
        messageRepository.save(msg);
        activityLogger.logSuccess(
                AiAssistantEntityTypes.AI_MESSAGE,
                cmd.messageId(),
                AiAssistantActivityActions.CANCEL_AI_MESSAGE,
                "Cancel requested for message"
        );
    }
}
