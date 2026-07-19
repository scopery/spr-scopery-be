package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.FORM_FIELD) @Getter @Setter @NoArgsConstructor
public class CustomFormFieldJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="form_version_id", nullable=false) private UUID formVersionId;
    @Column(name="section_id") private UUID sectionId;
    @Column(name="field_source", nullable=false) private String fieldSource;
    @Column(name="custom_field_definition_id") private UUID customFieldDefinitionId;
    @Column(name="core_field_key") private String coreFieldKey;
    @Column(name="label_override") private String labelOverride;
    @Column(name="required_on_form", nullable=false) private boolean requiredOnForm;
    @Column(name="readonly_flag", nullable=false) private boolean readonlyFlag;
    @Column(name="sort_order", nullable=false) private int sortOrder;
    @Column(name="metadata_json") private String metadataJson;
    @Version private Integer version;
}
