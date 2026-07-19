package com.company.scopery.modules.trust.privacy.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.PRIVACY_EXPORT_PACKAGE) @Getter @Setter @NoArgsConstructor
public class PrivacyExportPackageJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="privacy_request_id", nullable=false) private UUID privacyRequestId;
    @Column(name="data_subject_index_id", nullable=false) private UUID dataSubjectIndexId;
    @Column(nullable=false) private String status;
    @Column(name="package_manifest_json", columnDefinition="TEXT") private String packageManifestJson;
    @Column(name="file_reference") private String fileReference;
    @Column(name="expires_at") private Instant expiresAt;
    @Column(name="completed_at") private Instant completedAt;
    @Version private Integer version;
}
