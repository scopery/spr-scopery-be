package com.company.scopery.modules.externalparty.documentlink.infrastructure.mapper;

import com.company.scopery.modules.externalparty.documentlink.domain.model.ExternalPartyDocumentLink;
import com.company.scopery.modules.externalparty.documentlink.infrastructure.persistence.ExternalPartyDocumentLinkJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ExternalPartyDocumentLinkPersistenceMapper {

    public ExternalPartyDocumentLink toDomain(ExternalPartyDocumentLinkJpaEntity e) {
        return new ExternalPartyDocumentLink(e.getId(), e.getWorkspaceId(),
                e.getExternalOrganizationId(), e.getExternalContactId(),
                e.getDocumentId(), e.getLinkNote(),
                e.getVersion() != null ? e.getVersion() : 0,
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public ExternalPartyDocumentLinkJpaEntity toJpaEntity(ExternalPartyDocumentLink d) {
        var e = new ExternalPartyDocumentLinkJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setExternalOrganizationId(d.externalOrganizationId());
        e.setExternalContactId(d.externalContactId());
        e.setDocumentId(d.documentId());
        e.setLinkNote(d.linkNote());
        e.setCreatedAt(d.createdAt());
        e.setVersion(d.version());
        return e;
    }
}
