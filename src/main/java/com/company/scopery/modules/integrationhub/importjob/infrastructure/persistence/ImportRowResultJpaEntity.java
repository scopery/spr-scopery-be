package com.company.scopery.modules.integrationhub.importjob.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.IMPORT_ROW) @Getter @Setter @NoArgsConstructor
public class ImportRowResultJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="import_job_id", nullable=false) private UUID importJobId;
    @Column(name="row_number", nullable=false) private Long rowNumber;
    @Column(name="row_reference") private String rowReference;
    @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String message;
    @Column(name="validation_errors_json", columnDefinition="jsonb") private String validationErrorsJson;
    @Column(name="target_object_type") private String targetObjectType;
    @Column(name="target_object_id") private UUID targetObjectId;
    @Column(name="external_id") private String externalId;
    @Version private Integer version;
}
