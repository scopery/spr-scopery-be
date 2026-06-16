package com.company.scopery.modules.iam.grant.infrastructure.mapper;

import com.company.scopery.modules.iam.grant.domain.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.IamGrantScopeType;
import com.company.scopery.modules.iam.grant.domain.IamSubjectType;
import com.company.scopery.modules.iam.grant.infrastructure.persistence.IamAccessGrantJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IamAccessGrantPersistenceMapper {

    public IamAccessGrant toDomain(IamAccessGrantJpaEntity entity) {
        return new IamAccessGrant(
                entity.getId(),
                IamSubjectType.valueOf(entity.getSubjectType()),
                entity.getSubjectId(),
                entity.getResourceId(),
                entity.getRoleId(),
                entity.getEffect() != null ? IamGrantEffect.valueOf(entity.getEffect()) : IamGrantEffect.ALLOW,
                entity.getScopeType() != null ? IamGrantScopeType.valueOf(entity.getScopeType()) : null,
                entity.getScopeRefId(),
                entity.getWorkspaceId(),
                IamAccessGrantStatus.valueOf(entity.getStatus()),
                entity.getGrantedBy(),
                entity.getGrantedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public IamAccessGrantJpaEntity toJpaEntity(IamAccessGrant domain) {
        IamAccessGrantJpaEntity entity = new IamAccessGrantJpaEntity();
        entity.setId(domain.id());
        entity.setSubjectType(domain.subjectType().name());
        entity.setSubjectId(domain.subjectId());
        entity.setResourceId(domain.resourceId());
        entity.setRoleId(domain.roleId());
        entity.setEffect(domain.effect().name());
        entity.setScopeType(domain.scopeType() != null ? domain.scopeType().name() : null);
        entity.setScopeRefId(domain.scopeRefId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setStatus(domain.status().name());
        entity.setGrantedBy(domain.grantedBy());
        entity.setGrantedAt(domain.grantedAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
