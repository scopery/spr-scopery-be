package com.company.scopery.modules.configuration.layout.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.LAYOUT) @Getter @Setter @NoArgsConstructor
public class LayoutDefinitionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="layout_type", nullable=false) private String layoutType;
    @Column(nullable=false) private String name;
    @Column(name="layout_json", nullable=false) private String layoutJson;
    @Column(nullable=false) private String status;
    @Column(name="current_flag", nullable=false) private boolean currentFlag;
    @Version private Integer version;
}
