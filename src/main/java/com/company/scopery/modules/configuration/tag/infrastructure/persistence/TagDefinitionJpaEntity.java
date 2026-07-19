package com.company.scopery.modules.configuration.tag.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.TAG) @Getter @Setter @NoArgsConstructor
public class TagDefinitionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="tag_code", nullable=false) private String tagCode;
    @Column(nullable=false) private String label;
    private String color;
    @Column(name="allowed_object_types_json") private String allowedObjectTypesJson;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
