package com.company.scopery.modules.iam.role.infrastructure.mapper;

import com.company.scopery.modules.iam.role.domain.IamRole;
import com.company.scopery.modules.iam.role.domain.IamRoleCode;
import com.company.scopery.modules.iam.role.domain.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.IamRoleStatus;
import com.company.scopery.modules.iam.role.infrastructure.persistence.IamRoleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IamRolePersistenceMapper {

    public IamRole toDomain(IamRoleJpaEntity entity) {
        return new IamRole(
                entity.getId(),
                IamRoleCode.of(entity.getCode()),
                entity.getName(),
                entity.getDescription(),
                IamRoleStatus.valueOf(entity.getStatus()),
                entity.getRoleScope() != null ? IamRoleScope.valueOf(entity.getRoleScope()) : null,
                entity.getRoleSource() != null ? IamRoleSource.valueOf(entity.getRoleSource()) : null,
                entity.getWorkspaceId(),
                entity.getParentRoleId(),
                entity.getDeletedBy(),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public IamRoleJpaEntity toJpaEntity(IamRole domain) {
        IamRoleJpaEntity entity = new IamRoleJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code().value());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setStatus(domain.status().name());
        entity.setRoleScope(domain.roleScope() != null ? domain.roleScope().name() : null);
        entity.setRoleSource(domain.roleSource() != null ? domain.roleSource().name() : null);
        entity.setWorkspaceId(domain.workspaceId());
        entity.setParentRoleId(domain.parentRoleId());
        entity.setDeletedAt(domain.deletedAt());
        entity.setDeletedBy(domain.deletedBy());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
