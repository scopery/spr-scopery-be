package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.FORM) @Getter @Setter @NoArgsConstructor
public class CustomFormDefinitionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="form_code", nullable=false) private String formCode;
    @Column(nullable=false) private String name;
    @Column(name="form_type") private String formType;
    @Column(nullable=false) private String status;
    @Column(name="current_version_id") private UUID currentVersionId;
    @Version private Integer version;
}
