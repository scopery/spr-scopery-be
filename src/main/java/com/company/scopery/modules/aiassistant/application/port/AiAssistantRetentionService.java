package com.company.scopery.modules.aiassistant.application.port;

import com.company.scopery.modules.aiassistant.domain.enums.ConversationStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.domain.model.AiConversationRepository;
import com.company.scopery.modules.aiassistant.domain.model.AiStreamEventRepository;
import com.company.scopery.modules.aiassistant.shared.config.AiAssistantProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class AiAssistantRetentionService {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantRetentionService.class);

    private final AiConversationRepository conversationRepository;
    private final AiStreamEventRepository streamEventRepository;
    private final AiAssistantProperties properties;

    public AiAssistantRetentionService(AiConversationRepository conversationRepository,
                                       AiStreamEventRepository streamEventRepository,
                                       AiAssistantProperties properties) {
        this.conversationRepository = conversationRepository;
        this.streamEventRepository = streamEventRepository;
        this.properties = properties;
    }

    @Transactional
    public int purgeExpiredStreamEvents() {
        return streamEventRepository.deleteByExpiresAtBefore(Instant.now());
    }

    @Transactional
    public int softDeleteExpiredConversations() {
        Instant threshold = Instant.now().minus(properties.getConversationRetention());
        List<AiConversation> expired = conversationRepository
                .findByStatusAndLastMessageAtBefore(ConversationStatus.ACTIVE, threshold);
        for (AiConversation conversation : expired) {
            conversation.softDelete();
            conversationRepository.save(conversation);
        }
        return expired.size();
    }

    @Transactional
    public int purgeDeletedConversations() {
        Instant threshold = Instant.now().minus(properties.getDeletedPurgeDelay());
        List<AiConversation> toDelete = conversationRepository
                .findByStatusAndDeletedAtBefore(ConversationStatus.DELETED, threshold);
        if (!toDelete.isEmpty()) {
            log.warn("[AiAssistantRetentionService] Hard-delete of {} conversations deferred — " +
                    "physical purge not yet implemented in Phase 42.", toDelete.size());
        }
        return toDelete.size();
    }
}
