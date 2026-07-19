package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.enums.CapabilityLevel;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationStatus;
import com.company.scopery.modules.aiassistant.domain.enums.ConversationType;
import com.company.scopery.modules.aiassistant.domain.enums.TitleSource;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiConversationJpaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiConversationPersistenceMapper {

    public AiConversationJpaEntity toJpaEntity(AiConversation domain) {
        AiConversationJpaEntity entity = new AiConversationJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setOwnerUserId(domain.ownerUserId());
        entity.setConversationType(domain.conversationType() != null ? domain.conversationType().name() : null);
        entity.setCapabilityLevel(domain.capabilityLevel() != null ? domain.capabilityLevel().name() : null);
        entity.setAssistantAgentId(domain.assistantAgentId());
        entity.setStatus(domain.status() != null ? domain.status().name() : null);
        entity.setTitle(domain.title());
        entity.setTitleSource(domain.titleSource() != null ? domain.titleSource().name() : null);
        entity.setRetentionPolicyCode(domain.retentionPolicyCode());
        entity.setLastMessageAt(domain.lastMessageAt());
        entity.setLastMemorySummaryVersion(domain.lastMemorySummaryVersion());
        entity.setArchivedAt(domain.archivedAt());
        entity.setDeletedAt(domain.deletedAt());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }

    public AiConversation toDomain(AiConversationJpaEntity entity) {
        return AiConversation.reconstitute(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                entity.getOwnerUserId(),
                entity.getConversationType() != null ? ConversationType.valueOf(entity.getConversationType()) : null,
                entity.getCapabilityLevel() != null ? CapabilityLevel.valueOf(entity.getCapabilityLevel()) : null,
                entity.getAssistantAgentId(),
                entity.getStatus() != null ? ConversationStatus.valueOf(entity.getStatus()) : null,
                entity.getTitle(),
                entity.getTitleSource() != null ? TitleSource.valueOf(entity.getTitleSource()) : null,
                entity.getRetentionPolicyCode(),
                entity.getLastMessageAt(),
                entity.getLastMemorySummaryVersion(),
                entity.getArchivedAt(),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                parseUuid(entity.getCreatedBy()),
                entity.getUpdatedAt(),
                parseUuid(entity.getUpdatedBy()),
                entity.getVersion()
        );
    }

    private static UUID parseUuid(String value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
