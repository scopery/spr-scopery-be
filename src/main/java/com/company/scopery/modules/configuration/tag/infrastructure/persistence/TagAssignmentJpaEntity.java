package com.company.scopery.modules.configuration.tag.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.TAG_ASSIGN) @Getter @Setter @NoArgsConstructor
public class TagAssignmentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="tag_definition_id", nullable=false) private UUID tagDefinitionId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="archived_at") private Instant archivedAt;
    @Version private Integer version;
}
