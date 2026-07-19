package com.company.scopery.modules.configuration.objecttype.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.OBJECT_TYPE) @Getter @Setter @NoArgsConstructor
public class ConfigurableObjectTypeJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(nullable=false, unique=true) private String code;
    @Column(nullable=false) private String name;
    @Column(name="custom_fields_enabled", nullable=false) private boolean customFieldsEnabled;
    @Column(name="forms_enabled", nullable=false) private boolean formsEnabled;
    @Column(name="tags_enabled", nullable=false) private boolean tagsEnabled;
    @Column(name="custom_status_enabled", nullable=false) private boolean customStatusEnabled;
    @Column(name="client_visible_fields_enabled", nullable=false) private boolean clientVisibleFieldsEnabled;
    @Column(name="reportable_fields_enabled", nullable=false) private boolean reportableFieldsEnabled;
    @Column(name="searchable_fields_enabled", nullable=false) private boolean searchableFieldsEnabled;
    @Column(nullable=false) private boolean enabled;
    @Version private Integer version;
}
