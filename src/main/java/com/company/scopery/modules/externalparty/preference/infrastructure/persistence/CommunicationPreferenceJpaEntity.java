package com.company.scopery.modules.externalparty.preference.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity @Table(name = ExternalPartyTableNames.PREFERENCE) @Getter @Setter @NoArgsConstructor
public class CommunicationPreferenceJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "external_organization_id") private UUID externalOrganizationId;
    @Column(name = "external_contact_id") private UUID externalContactId;
    @Column(name = "preferred_channel_type") private String preferredChannelType;
    @Column(name = "preferred_language") private String preferredLanguage;
    @Column(name = "do_not_contact", nullable = false) private boolean doNotContact;
    @Column(columnDefinition = "text") private String notes;
    @Version private Integer version;
}
