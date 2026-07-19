package com.company.scopery.modules.integrationhub.importjob.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.IMPORT_JOB) @Getter @Setter @NoArgsConstructor
public class ImportJobJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="job_mode", nullable=false) private String jobMode;
    @Column(name="source_format", nullable=false) private String sourceFormat;
    @Column(name="target_object_type", nullable=false) private String targetObjectType;
    @Column(nullable=false) private String status;
    @Column(name="total_rows", nullable=false) private Long totalRows = 0L;
    @Column(name="valid_rows", nullable=false) private Long validRows = 0L;
    @Column(name="invalid_rows", nullable=false) private Long invalidRows = 0L;
    @Column(name="created_rows", nullable=false) private Long createdRows = 0L;
    @Version private Integer version;
}
