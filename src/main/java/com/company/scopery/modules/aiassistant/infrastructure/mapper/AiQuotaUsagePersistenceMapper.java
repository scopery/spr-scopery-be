package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.model.AiQuotaUsage;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiQuotaUsageJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiQuotaUsagePersistenceMapper {

    public AiQuotaUsageJpaEntity toJpaEntity(AiQuotaUsage domain) {
        AiQuotaUsageJpaEntity entity = new AiQuotaUsageJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setActorId(domain.actorId());
        entity.setUsageDate(domain.usageDate());
        entity.setTurnCount(domain.turnCount());
        entity.setInputTokenCount(domain.inputTokenCount());
        entity.setOutputTokenCount(domain.outputTokenCount());
        entity.setFailedTurnCount(domain.failedTurnCount());
        entity.setBlockedTurnCount(domain.blockedTurnCount());
        entity.setUpdatedAt(domain.updatedAt());
        entity.setVersion(domain.version());
        return entity;
    }

    public AiQuotaUsage toDomain(AiQuotaUsageJpaEntity entity) {
        return AiQuotaUsage.reconstitute(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getActorId(),
                entity.getUsageDate(),
                entity.getTurnCount(),
                entity.getInputTokenCount(),
                entity.getOutputTokenCount(),
                entity.getFailedTurnCount(),
                entity.getBlockedTurnCount(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
