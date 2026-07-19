package com.company.scopery.modules.aiagent.tool.infrastructure.mapper;

import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolMutationType;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolStatus;
import com.company.scopery.modules.aiagent.tool.domain.model.AiTool;
import com.company.scopery.modules.aiagent.tool.domain.valueobject.AiToolCode;
import com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity.AiToolJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiToolPersistenceMapper {

    public AiToolJpaEntity toJpaEntity(AiTool tool) {
        AiToolJpaEntity entity = new AiToolJpaEntity();
        entity.setId(tool.id());
        entity.setCode(tool.code().value());
        entity.setName(tool.name());
        entity.setDescription(tool.description());
        entity.setCategory(tool.category());
        entity.setMutationType(tool.mutationType().name());
        entity.setRequiresHumanApproval(tool.requiresHumanApproval());
        entity.setStatus(tool.status().name());
        if (tool.createdAt() != null) {
            entity.setCreatedAt(tool.createdAt());
        }
        return entity;
    }

    public AiTool toDomain(AiToolJpaEntity entity) {
        return AiTool.reconstitute(
                entity.getId(),
                AiToolCode.of(entity.getCode()),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                AiToolMutationType.valueOf(entity.getMutationType()),
                entity.isRequiresHumanApproval(),
                AiToolStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
