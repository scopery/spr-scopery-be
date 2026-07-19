package com.company.scopery.modules.externalparty.contact.infrastructure.mapper;
import com.company.scopery.modules.externalparty.contact.domain.enums.ContactStatus;
import com.company.scopery.modules.externalparty.contact.domain.model.ExternalContact;
import com.company.scopery.modules.externalparty.contact.infrastructure.persistence.ExternalContactJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExternalContactPersistenceMapper {
    public ExternalContact toDomain(ExternalContactJpaEntity e) {
        return new ExternalContact(e.getId(), e.getWorkspaceId(), e.getOrganizationId(), e.getFirstName(), e.getLastName(), e.getEmail(), e.getPhone(),
                e.getTitle(), ContactStatus.valueOf(e.getStatus()), Boolean.TRUE.equals(e.getPrimaryFlag()), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ExternalContactJpaEntity toJpaEntity(ExternalContact d) {
        ExternalContactJpaEntity e = new ExternalContactJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setOrganizationId(d.organizationId());
        e.setFirstName(d.firstName()); e.setLastName(d.lastName()); e.setEmail(d.email()); e.setPhone(d.phone());
        e.setTitle(d.title()); e.setStatus(d.status().name()); e.setPrimaryFlag(d.primaryFlag());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
