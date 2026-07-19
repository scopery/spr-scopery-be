package com.company.scopery.modules.trust.consent.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.CONSENT_RECORD) @Getter @Setter @NoArgsConstructor
public class ConsentRecordJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "consent_type", length = 100, nullable = false) private String consentType;
    @Column(nullable = false, length = 50) private String status;
    @Column(name = "given_at") private Instant givenAt;
    @Column(name = "withdrawn_at") private Instant withdrawnAt;
    @Version private Integer version;
}
