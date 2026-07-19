package com.company.scopery.modules.configuration.fieldvalue.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.time.*; import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.FIELD_VALUE) @Getter @Setter @NoArgsConstructor
public class CustomFieldValueJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="custom_field_definition_id", nullable=false) private UUID customFieldDefinitionId;
    @Column(name="value_text") private String valueText;
    @Column(name="value_long_text") private String valueLongText;
    @Column(name="value_number") private Double valueNumber;
    @Column(name="value_decimal") private BigDecimal valueDecimal;
    @Column(name="value_boolean") private Boolean valueBoolean;
    @Column(name="value_date") private LocalDate valueDate;
    @Column(name="value_datetime") private Instant valueDatetime;
    @Column(name="value_json") private String valueJson;
    @Column(name="value_option_ids") private String valueOptionIds;
    @Version private Integer version;
}
