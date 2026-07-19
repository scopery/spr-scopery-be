package com.company.scopery.modules.clientportal.account.infrastructure.mapper;
import com.company.scopery.modules.clientportal.account.domain.enums.PortalAccountStatus;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccount;
import com.company.scopery.modules.clientportal.account.infrastructure.persistence.ExternalPortalAccountJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExternalPortalAccountPersistenceMapper {
    public ExternalPortalAccount toDomain(ExternalPortalAccountJpaEntity e) {
        return new ExternalPortalAccount(e.getId(), e.getWorkspaceId(), e.getContactId(), e.getEmail(), e.getDisplayName(),
                PortalAccountStatus.valueOf(e.getStatus()), e.getPasswordHash(), e.getLastLoginAt(), e.getActivatedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ExternalPortalAccountJpaEntity toJpaEntity(ExternalPortalAccount d) {
        ExternalPortalAccountJpaEntity e = new ExternalPortalAccountJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setContactId(d.contactId()); e.setEmail(d.email());
        e.setDisplayName(d.displayName()); e.setStatus(d.status().name()); e.setPasswordHash(d.passwordHash());
        e.setLastLoginAt(d.lastLoginAt()); e.setActivatedAt(d.activatedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
