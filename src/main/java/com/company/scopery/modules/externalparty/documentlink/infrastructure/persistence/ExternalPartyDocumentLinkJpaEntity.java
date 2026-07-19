package com.company.scopery.modules.externalparty.documentlink.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity @Table(name = ExternalPartyTableNames.DOCUMENT_LINK) @Getter @Setter @NoArgsConstructor
public class ExternalPartyDocumentLinkJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "external_organization_id") private UUID externalOrganizationId;
    @Column(name = "external_contact_id") private UUID externalContactId;
    @Column(name = "document_id", nullable = false) private UUID documentId;
    @Column(name = "link_note", columnDefinition = "text") private String linkNote;
    @Version private Integer version;
}
