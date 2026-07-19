package com.company.scopery.modules.externalparty.organization.infrastructure.mapper;
import com.company.scopery.modules.externalparty.organization.domain.enums.*;
import com.company.scopery.modules.externalparty.organization.domain.model.ExternalOrganization;
import com.company.scopery.modules.externalparty.organization.infrastructure.persistence.ExternalOrganizationJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExternalOrganizationPersistenceMapper {
    public ExternalOrganization toDomain(ExternalOrganizationJpaEntity e) {
        return new ExternalOrganization(e.getId(), e.getWorkspaceId(), e.getCode(), e.getName(), OrganizationType.valueOf(e.getOrganizationType()),
                OrganizationStatus.valueOf(e.getStatus()), e.getTaxId(), e.getWebsite(), e.getNotes(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ExternalOrganizationJpaEntity toJpaEntity(ExternalOrganization d) {
        ExternalOrganizationJpaEntity e = new ExternalOrganizationJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setCode(d.code()); e.setName(d.name());
        e.setOrganizationType(d.organizationType().name()); e.setStatus(d.status().name());
        e.setTaxId(d.taxId()); e.setWebsite(d.website()); e.setNotes(d.notes());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
