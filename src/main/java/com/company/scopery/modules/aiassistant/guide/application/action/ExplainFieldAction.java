package com.company.scopery.modules.aiassistant.guide.application.action;

import com.company.scopery.modules.aiassistant.domain.enums.CapabilityLevel;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationType;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.domain.model.AiConversationRepository;
import com.company.scopery.modules.aiassistant.guide.application.command.ExplainFieldCommand;
import com.company.scopery.modules.aiassistant.message.application.response.AiSseStartResponse;
import com.company.scopery.modules.aiassistant.shared.activity.AiAssistantActivityLogger;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantActivityActions;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantApiPaths;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Creates a GENERAL_GUIDE-type conversation and dispatches to the orchestrator
 * with EXPLAIN_FIELD response mode.
 * Full guide-flow orchestration is deferred — this action returns a placeholder response.
 */
@Component
public class ExplainFieldAction {

    private final AiConversationRepository conversationRepository;
    private final AiAssistantActivityLogger activityLogger;

    public ExplainFieldAction(AiConversationRepository conversationRepository,
                              AiAssistantActivityLogger activityLogger) {
        this.conversationRepository = conversationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiSseStartResponse execute(ExplainFieldCommand cmd) {
        AiConversation conv = AiConversation.create(
                cmd.workspaceId(),
                cmd.projectId(),
                cmd.actorId(),
                ConversationType.GENERAL_GUIDE,
                CapabilityLevel.GUIDE,
                null,
                "Explain Field: " + cmd.pageCode() + "." + cmd.fieldCode()
        );
        AiConversation saved = conversationRepository.save(conv);

        // TODO: Full guide-flow orchestration (prompt dispatch, SSE) is deferred to concurrent agent
        UUID placeholderMessageId = UUID.randomUUID();
        UUID placeholderTurnId = UUID.randomUUID();
        String streamUrl = AiAssistantApiPaths.MESSAGES + "/" + placeholderMessageId + "/stream";

        activityLogger.logSuccess(
                AiAssistantEntityTypes.AI_CONVERSATION,
                saved.id(),
                AiAssistantActivityActions.EXPLAIN_FIELD,
                "Explain field requested for: " + cmd.pageCode() + "." + cmd.fieldCode()
        );
        return new AiSseStartResponse(saved.id(), null, placeholderMessageId, placeholderTurnId, streamUrl);
    }
}
