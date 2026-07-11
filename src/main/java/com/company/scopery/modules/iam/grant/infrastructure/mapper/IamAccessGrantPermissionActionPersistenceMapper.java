package com.company.scopery.modules.iam.grant.infrastructure.mapper;

import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionAction;
import com.company.scopery.modules.iam.grant.infrastructure.persistence.IamAccessGrantPermissionActionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IamAccessGrantPermissionActionPersistenceMapper {

    public IamAccessGrantPermissionAction toDomain(IamAccessGrantPermissionActionJpaEntity entity) {
        return new IamAccessGrantPermissionAction(
                entity.getGrantId(),
                entity.getPermissionActionId(),
                entity.getCreatedAt()
        );
    }

    public IamAccessGrantPermissionActionJpaEntity toJpaEntity(IamAccessGrantPermissionAction domain) {
        IamAccessGrantPermissionActionJpaEntity entity = new IamAccessGrantPermissionActionJpaEntity();
        entity.setGrantId(domain.grantId());
        entity.setPermissionActionId(domain.permissionActionId());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
