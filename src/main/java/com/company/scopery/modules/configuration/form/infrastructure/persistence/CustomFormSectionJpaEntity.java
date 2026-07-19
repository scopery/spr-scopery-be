package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.FORM_SECTION) @Getter @Setter @NoArgsConstructor
public class CustomFormSectionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="form_version_id", nullable=false) private UUID formVersionId;
    @Column(nullable=false) private String title;
    @Column(name="sort_order", nullable=false) private int sortOrder;
    @Column(name="visibility_json") private String visibilityJson;
    @Version private Integer version;
}
