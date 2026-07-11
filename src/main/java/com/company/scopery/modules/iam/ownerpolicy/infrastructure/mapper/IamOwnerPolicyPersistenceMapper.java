package com.company.scopery.modules.iam.ownerpolicy.infrastructure.mapper;

import com.company.scopery.modules.iam.ownerpolicy.domain.enums.IamInheritanceScope;
import com.company.scopery.modules.iam.ownerpolicy.domain.enums.IamOwnerPolicyStatus;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicy;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;
import com.company.scopery.modules.iam.ownerpolicy.infrastructure.persistence.IamOwnerPolicyJpaEntity;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IamOwnerPolicyPersistenceMapper {
    private final ObjectMapper objectMapper;
    public IamOwnerPolicyPersistenceMapper(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    public IamOwnerPolicy toDomain(IamOwnerPolicyJpaEntity entity) {
        try {
            List<IamOwnerPolicyAction> actions = objectMapper.readValue(
                    entity.getActionBundle(), new TypeReference<>() {});
            return new IamOwnerPolicy(entity.getId(), IamResourceType.valueOf(entity.getResourceType()),
                    entity.getPolicyVersion(), IamOwnerPolicyStatus.valueOf(entity.getStatus()), actions,
                    IamInheritanceScope.valueOf(entity.getInheritanceScope()), entity.isCanDelegate(),
                    entity.getDelegationDepth(), entity.getEffectiveFrom(), entity.getEffectiveTo(),
                    entity.getVersion(), entity.getCreatedAt(), entity.getUpdatedAt());
        } catch (Exception e) {
            throw new IllegalStateException("Invalid IAM owner policy action bundle", e);
        }
    }

    public IamOwnerPolicyJpaEntity toJpaEntity(IamOwnerPolicy domain) {
        try {
            IamOwnerPolicyJpaEntity entity = new IamOwnerPolicyJpaEntity();
            entity.setId(domain.id()); entity.setResourceType(domain.resourceType().name());
            entity.setPolicyVersion(domain.policyVersion()); entity.setStatus(domain.status().name());
            entity.setActionBundle(objectMapper.writeValueAsString(domain.actionBundle()));
            entity.setInheritanceScope(domain.inheritanceScope().name());
            entity.setCanDelegate(domain.canDelegate()); entity.setDelegationDepth(domain.delegationDepth());
            entity.setEffectiveFrom(domain.effectiveFrom()); entity.setEffectiveTo(domain.effectiveTo());
            entity.setVersion(domain.version()); entity.setCreatedAt(domain.createdAt());
            return entity;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot persist IAM owner policy action bundle", e);
        }
    }
}
