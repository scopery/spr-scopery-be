package com.company.scopery.modules.project.wbs.infrastructure.mapper;

import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.infrastructure.persistence.WbsNodeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WbsNodePersistenceMapper {

    public WbsNode toDomain(WbsNodeJpaEntity entity) {
        return new WbsNode(
                entity.getId(),
                entity.getProjectId(),
                entity.getProjectPhaseId(),
                entity.getParentId(),
                entity.getCode(),
                entity.getTitle(),
                entity.getDescription(),
                WbsNodeType.valueOf(entity.getNodeType()),
                entity.getLevel() != null ? entity.getLevel() : 0,
                entity.getPath(),
                entity.getSortOrder() != null ? entity.getSortOrder() : 0,
                WbsNodeStatus.valueOf(entity.getStatus()),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public WbsNodeJpaEntity toJpaEntity(WbsNode domain) {
        WbsNodeJpaEntity entity = new WbsNodeJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setProjectPhaseId(domain.projectPhaseId());
        entity.setParentId(domain.parentId());
        entity.setCode(domain.code());
        entity.setTitle(domain.title());
        entity.setDescription(domain.description());
        entity.setNodeType(domain.nodeType().name());
        entity.setLevel(domain.level());
        entity.setPath(domain.path());
        entity.setSortOrder(domain.sortOrder());
        entity.setStatus(domain.status().name());
        entity.setVersion(domain.version());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}
