package com.company.scopery.modules.trust.exportaudit.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.EXPORT_AUDIT_LOG) @Getter @Setter @NoArgsConstructor
public class ExportAuditLogJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="export_type", nullable=false) private String exportType;
    @Column(name="target_object_type") private String targetObjectType;
    @Column(nullable=false) private String classification;
    @Column(name="row_count") private Long rowCount;
    @Column(name="file_reference") private String fileReference;
    private String reason;
    @Column(nullable=false) private String status;
    @Column(name="completed_at") private Instant completedAt;
    @Version private Integer version;
}
