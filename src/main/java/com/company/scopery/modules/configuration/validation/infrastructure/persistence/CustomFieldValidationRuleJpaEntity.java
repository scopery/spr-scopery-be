package com.company.scopery.modules.configuration.validation.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.FIELD_VALIDATION) @Getter @Setter @NoArgsConstructor
public class CustomFieldValidationRuleJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="custom_field_definition_id", nullable=false) private UUID customFieldDefinitionId;
    @Column(name="rule_type", nullable=false) private String ruleType;
    @Column(name="rule_config_json") private String ruleConfigJson;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
