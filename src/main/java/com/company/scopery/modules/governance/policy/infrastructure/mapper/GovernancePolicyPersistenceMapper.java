package com.company.scopery.modules.governance.policy.infrastructure.mapper;
import com.company.scopery.modules.governance.policy.domain.model.GovernancePolicy;
import com.company.scopery.modules.governance.policy.infrastructure.persistence.GovernancePolicyJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class GovernancePolicyPersistenceMapper {
    public GovernancePolicy toDomain(GovernancePolicyJpaEntity e) {
        return new GovernancePolicy(e.getId(), e.getWorkspaceId(), e.getObjectTypeCode(), e.getVersioningMode(), e.isVersionOnUpdate(),
                e.isLockOnFinalize(), e.isAllowRestore(), e.isAllowOwnerGrant(), e.getBaselineGuardMode(), e.getAuditLevel(),
                e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public GovernancePolicyJpaEntity toJpa(GovernancePolicy d) {
        GovernancePolicyJpaEntity e = new GovernancePolicyJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setObjectTypeCode(d.objectTypeCode()); e.setVersioningMode(d.versioningMode());
        e.setVersionOnUpdate(d.versionOnUpdate()); e.setLockOnFinalize(d.lockOnFinalize()); e.setAllowRestore(d.allowRestore());
        e.setAllowOwnerGrant(d.allowOwnerGrant()); e.setBaselineGuardMode(d.baselineGuardMode()); e.setAuditLevel(d.auditLevel());
        e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
