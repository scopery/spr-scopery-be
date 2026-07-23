package com.company.scopery.modules.aiassistant.message.application.action;

import com.company.scopery.modules.aiassistant.application.orchestrator.AiAssistantTurnOrchestrator;
import com.company.scopery.modules.aiassistant.application.service.AiAssistantQuotaService;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.domain.model.AiConversationRepository;
import com.company.scopery.modules.aiassistant.domain.model.AiMessage;
import com.company.scopery.modules.aiassistant.domain.model.AiMessageRepository;
import com.company.scopery.modules.aiassistant.message.application.command.SendMessageCommand;
import com.company.scopery.modules.aiassistant.message.application.response.AiSseStartResponse;
import com.company.scopery.modules.aiassistant.shared.activity.AiAssistantActivityLogger;
import com.company.scopery.modules.aiassistant.shared.config.AiAssistantProperties;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantActivityActions;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantApiPaths;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantEntityTypes;
import com.company.scopery.modules.aiassistant.shared.error.AiAssistantExceptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Executor;

@Component
public class SendMessageAction {

    private final AiConversationRepository conversationRepository;
    private final AiMessageRepository messageRepository;
    private final AiAssistantQuotaService quotaService;
    private final AiAssistantProperties properties;
    private final AiAssistantActivityLogger activityLogger;
    private final AiAssistantTurnOrchestrator orchestrator;
    private final Executor executor;

    public SendMessageAction(AiConversationRepository conversationRepository,
                             AiMessageRepository messageRepository,
                             AiAssistantQuotaService quotaService,
                             AiAssistantProperties properties,
                             AiAssistantActivityLogger activityLogger,
                             AiAssistantTurnOrchestrator orchestrator,
                             @Qualifier("aiAssistantExecutor") Executor executor) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.quotaService = quotaService;
        this.properties = properties;
        this.activityLogger = activityLogger;
        this.orchestrator = orchestrator;
        this.executor = executor;
    }

    @Transactional
    public AiSseStartResponse execute(SendMessageCommand cmd) {
        // 1. Load + validate conversation
        AiConversation conv = conversationRepository
                .findByIdAndOwnerUserId(cmd.conversationId(), cmd.actorId())
                .orElseThrow(() -> AiAssistantExceptions.conversationNotFound(cmd.conversationId()));
        if (conv.status() != ConversationStatus.ACTIVE) {
            throw AiAssistantExceptions.conversationNotFound(cmd.conversationId());
        }

        // 2. Validate message length
        if (cmd.content() != null && cmd.content().length() > properties.getMaxUserMessageChars()) {
            throw AiAssistantExceptions.messageTooLong(cmd.content().length(), properties.getMaxUserMessageChars());
        }

        // 3. Check idempotency
        if (cmd.idempotencyKey() != null) {
            messageRepository.findByConversationIdAndIdempotencyKey(conv.id(), cmd.idempotencyKey())
                    .ifPresent(m -> {
                        throw AiAssistantExceptions.messageIdempotencyConflict(conv.id(), cmd.idempotencyKey());
                    });
        }

        // 4. Check message count
        int count = messageRepository.countByConversationId(conv.id());
        if (count >= properties.getMaxMessagesPerConversation()) {
            throw AiAssistantExceptions.conversationMessageLimitExceeded(conv.id(), properties.getMaxMessagesPerConversation());
        }

        // 5. Create turnId, user message, and assistant placeholder
        UUID turnId = UUID.randomUUID();
        AiMessage userMsg = AiMessage.createUser(conv.id(), turnId, count + 1,
                cmd.content(), cmd.idempotencyKey(), null);
        AiMessage savedUser = messageRepository.save(userMsg);

        AiMessage assistantMsg = AiMessage.createAssistantPlaceholder(conv.id(), turnId, count + 2, null);
        AiMessage savedAssistant = messageRepository.save(assistantMsg);

        // 6. Touch conversation lastMessageAt
        conv.touchLastMessage(Instant.now());
        conversationRepository.save(conv);

        // 7. Reserve quota turn
        quotaService.reserveTurn(cmd.actorId(), cmd.workspaceId());

        // 8. Submit async turn execution
        UUID resolvedProjectId = cmd.projectId() != null ? cmd.projectId() : conv.projectId();
        AiAssistantTurnOrchestrator.TurnRequest turnReq = new AiAssistantTurnOrchestrator.TurnRequest(
                conv.id(),
                savedAssistant.id(),
                savedUser.id(),
                turnId,
                cmd.actorId(),
                cmd.workspaceId(),
                resolvedProjectId,
                cmd.content(),
                properties.getPromptProfileCode(),
                null, // modelDeploymentId — resolved by orchestrator
                cmd.modelProvider(),
                cmd.modelName()
        );
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                executor.execute(() -> orchestrator.executeTurn(turnReq));
            }
        });

        // 9. Return SSE start response
        String streamUrl = AiAssistantApiPaths.MESSAGES + "/" + savedAssistant.id() + "/stream";
        activityLogger.logSuccess(
                AiAssistantEntityTypes.AI_MESSAGE,
                savedAssistant.id(),
                AiAssistantActivityActions.SEND_AI_MESSAGE,
                "Turn started"
        );
        return new AiSseStartResponse(conv.id(), savedUser.id(), savedAssistant.id(), turnId, streamUrl);
    }
}
