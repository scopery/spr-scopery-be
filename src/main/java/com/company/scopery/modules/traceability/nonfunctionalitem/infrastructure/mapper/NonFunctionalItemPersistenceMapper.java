package com.company.scopery.modules.traceability.nonfunctionalitem.infrastructure.mapper;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrCategory;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrPriority;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrScopeType;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.enums.NfrStatus;
import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItem;
import com.company.scopery.modules.traceability.nonfunctionalitem.infrastructure.persistence.NonFunctionalItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class NonFunctionalItemPersistenceMapper {
    public NonFunctionalItem toDomain(NonFunctionalItemJpaEntity entity) {
        return new NonFunctionalItem(
                entity.getId(),
                entity.getProjectId(),
                entity.getWorkspaceId(),
                entity.getCode(),
                entity.getTitle(),
                entity.getDescription(),
                NfrCategory.valueOf(entity.getCategory()),
                NfrPriority.valueOf(entity.getPriority()),
                NfrStatus.valueOf(entity.getStatus()),
                entity.getTargetMetric(),
                NfrScopeType.valueOf(entity.getScopeType()),
                entity.getScopeRefId(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    public NonFunctionalItemJpaEntity toJpaEntity(NonFunctionalItem domain) {
        NonFunctionalItemJpaEntity entity = new NonFunctionalItemJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setCode(domain.code());
        entity.setTitle(domain.title());
        entity.setDescription(domain.description());
        entity.setCategory(domain.category().name());
        entity.setPriority(domain.priority().name());
        entity.setStatus(domain.status().name());
        entity.setTargetMetric(domain.targetMetric());
        entity.setScopeType(domain.scopeType().name());
        entity.setScopeRefId(domain.scopeRefId());
        // New: leave version/createdAt null → persist. Update: stamp both for optimistic lock.
        if (domain.createdAt() != null) {
            entity.setVersion(domain.version());
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}
