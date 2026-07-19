package com.company.scopery.modules.trust.consent.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.CONTACT_SUPPRESSION) @Getter @Setter @NoArgsConstructor
public class ContactSuppressionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "external_contact_id") private UUID externalContactId;
    @Column(name = "portal_account_id") private UUID portalAccountId;
    @Column(name = "suppression_type", length = 100, nullable = false) private String suppressionType;
    @Column(columnDefinition = "TEXT") private String reason;
    @Column(nullable = false, length = 50) private String status;
    @Column(name = "released_at") private Instant releasedAt;
    @Column(name = "release_reason", columnDefinition = "TEXT") private String releaseReason;
    @Version private Integer version;
}
