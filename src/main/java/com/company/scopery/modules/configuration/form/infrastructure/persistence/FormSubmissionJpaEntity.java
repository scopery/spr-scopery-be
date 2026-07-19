package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.FORM_SUBMISSION) @Getter @Setter @NoArgsConstructor
public class FormSubmissionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="form_definition_id", nullable=false) private UUID formDefinitionId;
    @Column(name="form_version_id", nullable=false) private UUID formVersionId;
    @Column(name="object_type_code") private String objectTypeCode;
    @Column(name="target_id") private UUID targetId;
    @Column(name="principal_type", nullable=false) private String principalType;
    @Column(name="submitted_by") private UUID submittedBy;
    @Column(name="payload_json", nullable=false) private String payloadJson;
    @Column(name="validation_status", nullable=false) private String validationStatus;
    @Column(name="validation_errors_json") private String validationErrorsJson;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
