package com.company.scopery.modules.configuration.fieldoption.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.FIELD_OPTION) @Getter @Setter @NoArgsConstructor
public class CustomFieldOptionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="custom_field_definition_id", nullable=false) private UUID customFieldDefinitionId;
    @Column(name="option_code", nullable=false) private String optionCode;
    @Column(nullable=false) private String label;
    @Column(name="sort_order", nullable=false) private int sortOrder;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
