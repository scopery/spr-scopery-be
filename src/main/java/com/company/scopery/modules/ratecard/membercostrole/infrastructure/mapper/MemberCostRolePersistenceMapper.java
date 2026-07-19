package com.company.scopery.modules.ratecard.membercostrole.infrastructure.mapper;

import com.company.scopery.modules.ratecard.membercostrole.domain.enums.MemberCostRoleStatus;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleAssignment;
import com.company.scopery.modules.ratecard.membercostrole.infrastructure.persistence.MemberCostRoleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberCostRolePersistenceMapper {
    public WorkspaceMemberCostRoleAssignment toDomain(MemberCostRoleJpaEntity e) {
        return new WorkspaceMemberCostRoleAssignment(e.getId(), e.getWorkspaceId(), e.getWorkspaceMemberId(),
                e.getUserId(), e.getCostRoleId(), e.isDefault(), e.getEffectiveFrom(), e.getEffectiveTo(),
                MemberCostRoleStatus.valueOf(e.getStatus()), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() != null ? e.getVersion() : 0, e.getCreatedAt(), e.getUpdatedAt());
    }
    public MemberCostRoleJpaEntity toJpaEntity(WorkspaceMemberCostRoleAssignment d) {
        MemberCostRoleJpaEntity e = new MemberCostRoleJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setWorkspaceMemberId(d.workspaceMemberId());
        e.setUserId(d.userId()); e.setCostRoleId(d.costRoleId()); e.setDefault(d.isDefault());
        e.setEffectiveFrom(d.effectiveFrom()); e.setEffectiveTo(d.effectiveTo());
        e.setStatus(d.status().name()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
