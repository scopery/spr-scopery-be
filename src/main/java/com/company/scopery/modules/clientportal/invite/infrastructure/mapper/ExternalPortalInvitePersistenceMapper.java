package com.company.scopery.modules.clientportal.invite.infrastructure.mapper;
import com.company.scopery.modules.clientportal.invite.domain.enums.PortalInviteStatus;
import com.company.scopery.modules.clientportal.invite.domain.model.ExternalPortalInvite;
import com.company.scopery.modules.clientportal.invite.infrastructure.persistence.ExternalPortalInviteJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExternalPortalInvitePersistenceMapper {
    public ExternalPortalInvite toDomain(ExternalPortalInviteJpaEntity e) {
        return new ExternalPortalInvite(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getEmail(), e.getInviteTokenHash(),
                PortalInviteStatus.valueOf(e.getStatus()), e.getExpiresAt(), e.getAcceptedAt(), e.getInvitedBy(), e.getPortalAccountId(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ExternalPortalInviteJpaEntity toJpaEntity(ExternalPortalInvite d) {
        ExternalPortalInviteJpaEntity e = new ExternalPortalInviteJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setEmail(d.email());
        e.setInviteTokenHash(d.inviteTokenHash()); e.setStatus(d.status().name()); e.setExpiresAt(d.expiresAt());
        e.setAcceptedAt(d.acceptedAt()); e.setInvitedBy(d.invitedBy()); e.setPortalAccountId(d.portalAccountId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
