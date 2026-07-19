package com.company.scopery.modules.aiassistant.conversation.application.action;

import com.company.scopery.modules.aiassistant.conversation.application.command.ArchiveConversationCommand;
import com.company.scopery.modules.aiassistant.conversation.application.response.AiConversationResponse;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.domain.model.AiConversationRepository;
import com.company.scopery.modules.aiassistant.shared.activity.AiAssistantActivityLogger;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantActivityActions;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantEntityTypes;
import com.company.scopery.modules.aiassistant.shared.error.AiAssistantExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveConversationAction {

    private final AiConversationRepository conversationRepository;
    private final AiAssistantActivityLogger activityLogger;

    public ArchiveConversationAction(AiConversationRepository conversationRepository,
                                     AiAssistantActivityLogger activityLogger) {
        this.conversationRepository = conversationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiConversationResponse execute(ArchiveConversationCommand cmd) {
        AiConversation conv = conversationRepository
                .findByIdAndOwnerUserId(cmd.conversationId(), cmd.actorId())
                .orElseThrow(() -> AiAssistantExceptions.conversationNotFound(cmd.conversationId()));
        conv.archive();
        AiConversation saved = conversationRepository.save(conv);
        activityLogger.logSuccess(
                AiAssistantEntityTypes.AI_CONVERSATION,
                saved.id(),
                AiAssistantActivityActions.ARCHIVE_AI_CONVERSATION,
                "Conversation archived"
        );
        return AiConversationResponse.from(saved);
    }
}
