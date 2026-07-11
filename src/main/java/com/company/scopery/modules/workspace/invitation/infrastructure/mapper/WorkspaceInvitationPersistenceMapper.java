package com.company.scopery.modules.workspace.invitation.infrastructure.mapper;

import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitation;
import com.company.scopery.modules.workspace.invitation.domain.enums.WorkspaceInvitationStatus;
import com.company.scopery.modules.workspace.invitation.infrastructure.persistence.WorkspaceInvitationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceInvitationPersistenceMapper {

    public WorkspaceInvitation toDomain(WorkspaceInvitationJpaEntity e) {
        return new WorkspaceInvitation(
                e.getId(), e.getWorkspaceId(), e.getCreatedByUserId(), e.getInvitedEmail(),
                e.getInvitationCodeHash(), e.getInvitationCodeHint(),
                WorkspaceInvitationStatus.valueOf(e.getStatus()),
                e.getMaxUses(), e.getUsedCount(), e.getExpiresAt(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public WorkspaceInvitationJpaEntity toJpaEntity(WorkspaceInvitation d) {
        WorkspaceInvitationJpaEntity e = new WorkspaceInvitationJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setCreatedByUserId(d.createdByUserId());
        e.setInvitedEmail(d.invitedEmail());
        e.setInvitationCodeHash(d.invitationCodeHash());
        e.setInvitationCodeHint(d.invitationCodeHint());
        e.setStatus(d.status().name());
        e.setMaxUses(d.maxUses());
        e.setUsedCount(d.usedCount());
        e.setExpiresAt(d.expiresAt());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}
