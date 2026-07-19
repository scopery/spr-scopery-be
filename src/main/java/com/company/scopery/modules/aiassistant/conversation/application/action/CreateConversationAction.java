package com.company.scopery.modules.aiassistant.conversation.application.action;

import com.company.scopery.modules.aiassistant.conversation.application.command.CreateConversationCommand;
import com.company.scopery.modules.aiassistant.conversation.application.response.AiConversationResponse;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.domain.model.AiConversationRepository;
import com.company.scopery.modules.aiassistant.shared.activity.AiAssistantActivityLogger;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantActivityActions;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateConversationAction {

    private final AiConversationRepository conversationRepository;
    private final AiAssistantActivityLogger activityLogger;

    public CreateConversationAction(AiConversationRepository conversationRepository,
                                    AiAssistantActivityLogger activityLogger) {
        this.conversationRepository = conversationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiConversationResponse execute(CreateConversationCommand cmd) {
        AiConversation conv = AiConversation.create(
                cmd.workspaceId(),
                cmd.projectId(),
                cmd.actorId(),
                cmd.conversationType(),
                cmd.capabilityLevel(),
                cmd.assistantAgentId(),
                cmd.title()
        );
        AiConversation saved = conversationRepository.save(conv);
        activityLogger.logSuccess(
                AiAssistantEntityTypes.AI_CONVERSATION,
                saved.id(),
                AiAssistantActivityActions.CREATE_AI_CONVERSATION,
                "Conversation created"
        );
        return AiConversationResponse.from(saved);
    }
}
