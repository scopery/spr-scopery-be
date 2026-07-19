package com.company.scopery.modules.integrationhub.exportjob.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.EXPORT_JOB) @Getter @Setter @NoArgsConstructor
public class ExportJobJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="export_format", nullable=false) private String exportFormat;
    @Column(name="object_scope", nullable=false) private String objectScope;
    @Column(nullable=false) private String status;
    @Column(name="row_count") private Long rowCount;
    @Column(name="file_reference") private String fileReference;
    @Column(name="export_audit_log_id") private UUID exportAuditLogId;
    @Version private Integer version;
}
