package com.company.scopery.modules.configuration.customfield.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.FIELD_DEF) @Getter @Setter @NoArgsConstructor
public class CustomFieldDefinitionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="field_key", nullable=false) private String fieldKey;
    @Column(nullable=false) private String label;
    @Column(name="field_type", nullable=false) private String fieldType;
    @Column(name="required_flag", nullable=false) private boolean requiredFlag;
    @Column(name="sensitive_flag", nullable=false) private boolean sensitiveFlag;
    @Column(name="client_visible", nullable=false) private boolean clientVisible;
    @Column(nullable=false) private boolean searchable;
    @Column(nullable=false) private boolean reportable;
    @Column(nullable=false) private boolean exportable;
    @Column(name="default_value_json") private String defaultValueJson;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
