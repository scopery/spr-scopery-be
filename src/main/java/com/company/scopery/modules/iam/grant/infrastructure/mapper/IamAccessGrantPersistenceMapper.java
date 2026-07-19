package com.company.scopery.modules.iam.grant.infrastructure.mapper;

import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantScopeType;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantKind;
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
                IamGrantKind.valueOf(entity.getKind()),
                entity.getSourcePolicyId(),
                entity.isCanDelegate(),
                entity.getDelegationDepth(),
                entity.getExpiresAt(),
                entity.getConditionJson(),
                entity.getReason(),
                IamAccessGrantStatus.valueOf(entity.getStatus()),
                entity.getGrantedBy(),
                entity.getGrantedAt(),
                entity.getVersion(),
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
        entity.setKind(domain.kind().name());
        entity.setSourcePolicyId(domain.sourcePolicyId());
        entity.setCanDelegate(domain.canDelegate());
        entity.setDelegationDepth(domain.delegationDepth());
        entity.setExpiresAt(domain.expiresAt());
        entity.setConditionJson(domain.conditionJson());
        entity.setReason(domain.reason());
        entity.setStatus(domain.status().name());
        entity.setGrantedBy(domain.grantedBy());
        entity.setGrantedAt(domain.grantedAt());
        if (domain.createdAt() != null) { entity.setCreatedAt(domain.createdAt()); entity.setVersion(domain.version()); }
        return entity;
    }
}
