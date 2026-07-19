package com.company.scopery.modules.configuration.fieldvisibility.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.FIELD_VISIBILITY) @Getter @Setter @NoArgsConstructor
public class FieldVisibilityPolicyJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="custom_field_definition_id", nullable=false) private UUID customFieldDefinitionId;
    @Column(name="audience_type", nullable=false) private String audienceType;
    @Column(nullable=false) private boolean visible;
}
