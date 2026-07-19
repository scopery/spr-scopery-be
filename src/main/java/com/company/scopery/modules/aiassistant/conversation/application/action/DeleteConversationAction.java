package com.company.scopery.modules.aiassistant.conversation.application.action;

import com.company.scopery.modules.aiassistant.conversation.application.command.DeleteConversationCommand;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.domain.model.AiConversationRepository;
import com.company.scopery.modules.aiassistant.shared.activity.AiAssistantActivityLogger;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantActivityActions;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantEntityTypes;
import com.company.scopery.modules.aiassistant.shared.error.AiAssistantExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteConversationAction {

    private final AiConversationRepository conversationRepository;
    private final AiAssistantActivityLogger activityLogger;

    public DeleteConversationAction(AiConversationRepository conversationRepository,
                                    AiAssistantActivityLogger activityLogger) {
        this.conversationRepository = conversationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteConversationCommand cmd) {
        AiConversation conv = conversationRepository
                .findByIdAndOwnerUserId(cmd.conversationId(), cmd.actorId())
                .orElseThrow(() -> AiAssistantExceptions.conversationNotFound(cmd.conversationId()));
        conv.softDelete();
        conversationRepository.save(conv);
        activityLogger.logSuccess(
                AiAssistantEntityTypes.AI_CONVERSATION,
                cmd.conversationId(),
                AiAssistantActivityActions.DELETE_AI_CONVERSATION,
                "Conversation deleted"
        );
    }
}
