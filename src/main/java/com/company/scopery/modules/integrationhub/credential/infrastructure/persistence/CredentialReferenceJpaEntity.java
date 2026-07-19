package com.company.scopery.modules.integrationhub.credential.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.CREDENTIAL) @Getter @Setter @NoArgsConstructor
public class CredentialReferenceJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="provider_code", nullable=false) private String providerCode;
    @Column(name="credential_type", nullable=false) private String credentialType;
    @Column(name="secret_reference", nullable=false) private String secretReference;
    @Column(nullable=false) private String status;
    @Column(name="last_rotated_at") private Instant lastRotatedAt;
    @Column(name="revoked_at") private Instant revokedAt;
    @Version private Integer version;
}
