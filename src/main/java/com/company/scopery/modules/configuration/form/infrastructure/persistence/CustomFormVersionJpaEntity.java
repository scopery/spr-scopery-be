package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.FORM_VERSION) @Getter @Setter @NoArgsConstructor
public class CustomFormVersionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="form_definition_id", nullable=false) private UUID formDefinitionId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="version_number", nullable=false) private int versionNumber;
    @Column(nullable=false) private String status;
    @Column(name="schema_json") private String schemaJson;
    @Column(name="published_at") private Instant publishedAt;
    @Column(name="current_flag", nullable=false) private boolean currentFlag;
    @Version private Integer version;
}
