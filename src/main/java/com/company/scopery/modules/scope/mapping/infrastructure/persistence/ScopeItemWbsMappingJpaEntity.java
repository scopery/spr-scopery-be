package com.company.scopery.modules.scope.mapping.infrastructure.persistence;
import com.company.scopery.modules.scope.shared.constant.ScopeTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ScopeTableNames.ITEM_WBS) @Getter @Setter @NoArgsConstructor
public class ScopeItemWbsMappingJpaEntity {
    @Id private UUID id;
    @Column(name="scope_item_id", nullable=false) private UUID scopeItemId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="wbs_node_id", nullable=false) private UUID wbsNodeId;
    @Column(name="mapping_type", nullable=false) private String mappingType;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="created_by") private String createdBy;
}
