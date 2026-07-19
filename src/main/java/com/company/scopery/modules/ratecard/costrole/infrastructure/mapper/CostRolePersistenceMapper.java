package com.company.scopery.modules.ratecard.costrole.infrastructure.mapper;

import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.infrastructure.persistence.CostRoleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CostRolePersistenceMapper {
    public CostRole toDomain(CostRoleJpaEntity entity) {
        return new CostRole(
                entity.getId(), entity.getCode(), entity.getName(), entity.getDescription(),
                CostRoleScope.valueOf(entity.getScope()), entity.getOrganizationId(), entity.getWorkspaceId(),
                entity.getCategory(), entity.isBuiltIn(), CostRoleStatus.valueOf(entity.getStatus()),
                entity.getArchivedAt(), entity.getArchivedBy(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }

    public CostRoleJpaEntity toJpaEntity(CostRole domain) {
        CostRoleJpaEntity entity = new CostRoleJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setScope(domain.scope().name());
        entity.setOrganizationId(domain.organizationId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setCategory(domain.category());
        entity.setBuiltIn(domain.builtIn());
        entity.setStatus(domain.status().name());
        entity.setArchivedAt(domain.archivedAt());
        entity.setArchivedBy(domain.archivedBy());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
