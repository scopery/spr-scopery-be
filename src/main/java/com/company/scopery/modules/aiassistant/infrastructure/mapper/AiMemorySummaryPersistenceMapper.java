package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.enums.MemorySummaryStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiMemorySummary;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiMemorySummaryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiMemorySummaryPersistenceMapper {

    public AiMemorySummaryJpaEntity toJpaEntity(AiMemorySummary domain) {
        AiMemorySummaryJpaEntity entity = new AiMemorySummaryJpaEntity();
        entity.setId(domain.id());
        entity.setConversationId(domain.conversationId());
        entity.setSummaryVersion(domain.summaryVersion());
        entity.setStrategyCode(domain.strategyCode());
        entity.setStatus(domain.status() != null ? domain.status().name() : null);
        entity.setCoveredThroughMessageSequence(domain.coveredThroughMessageSequence());
        entity.setSourceMessageCount(domain.sourceMessageCount());
        entity.setEstimatedTokenCount(domain.estimatedTokenCount());
        entity.setSummaryText(domain.summaryText());
        entity.setPermissionSignature(domain.permissionSignature());
        entity.setSummaryHash(domain.summaryHash());
        entity.setModelProvider(domain.modelProvider());
        entity.setModelName(domain.modelName());
        entity.setPromptProfileCode(domain.promptProfileCode());
        entity.setCreatedAt(domain.createdAt());
        entity.setCreatedBy(domain.createdBy());
        entity.setInvalidatedAt(domain.invalidatedAt());
        entity.setInvalidationReasonCode(domain.invalidationReasonCode());
        return entity;
    }

    public AiMemorySummary toDomain(AiMemorySummaryJpaEntity entity) {
        return AiMemorySummary.reconstitute(
                entity.getId(),
                entity.getConversationId(),
                entity.getSummaryVersion(),
                entity.getStrategyCode(),
                entity.getStatus() != null ? MemorySummaryStatus.valueOf(entity.getStatus()) : null,
                entity.getCoveredThroughMessageSequence(),
                entity.getSourceMessageCount(),
                entity.getEstimatedTokenCount(),
                entity.getSummaryText(),
                entity.getPermissionSignature(),
                entity.getSummaryHash(),
                entity.getModelProvider(),
                entity.getModelName(),
                entity.getPromptProfileCode(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getInvalidatedAt(),
                entity.getInvalidationReasonCode()
        );
    }
}
