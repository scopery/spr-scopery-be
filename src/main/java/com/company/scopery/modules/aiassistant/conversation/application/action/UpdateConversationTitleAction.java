package com.company.scopery.modules.aiassistant.conversation.application.action;

import com.company.scopery.modules.aiassistant.conversation.application.command.UpdateConversationTitleCommand;
import com.company.scopery.modules.aiassistant.conversation.application.response.AiConversationResponse;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.domain.model.AiConversationRepository;
import com.company.scopery.modules.aiassistant.shared.activity.AiAssistantActivityLogger;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantActivityActions;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantEntityTypes;
import com.company.scopery.modules.aiassistant.shared.error.AiAssistantExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateConversationTitleAction {

    private final AiConversationRepository conversationRepository;
    private final AiAssistantActivityLogger activityLogger;

    public UpdateConversationTitleAction(AiConversationRepository conversationRepository,
                                         AiAssistantActivityLogger activityLogger) {
        this.conversationRepository = conversationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiConversationResponse execute(UpdateConversationTitleCommand cmd) {
        AiConversation conv = conversationRepository
                .findByIdAndOwnerUserId(cmd.conversationId(), cmd.actorId())
                .orElseThrow(() -> AiAssistantExceptions.conversationNotFound(cmd.conversationId()));
        if (conv.status() == ConversationStatus.DELETED) {
            throw AiAssistantExceptions.conversationNotFound(cmd.conversationId());
        }
        conv.updateTitle(cmd.title());
        AiConversation saved = conversationRepository.save(conv);
        activityLogger.logSuccess(
                AiAssistantEntityTypes.AI_CONVERSATION,
                saved.id(),
                AiAssistantActivityActions.UPDATE_AI_CONVERSATION_TITLE,
                "Title updated"
        );
        return AiConversationResponse.from(saved);
    }
}
