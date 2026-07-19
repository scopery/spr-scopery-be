package com.company.scopery.modules.scope.mapping.infrastructure.persistence;
import com.company.scopery.modules.scope.shared.constant.ScopeTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ScopeTableNames.DELIVERABLE_TASK) @Getter @Setter @NoArgsConstructor
public class DeliverableTaskMappingJpaEntity {
    @Id private UUID id;
    @Column(name="deliverable_id", nullable=false) private UUID deliverableId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="task_id", nullable=false) private UUID taskId;
    @Column(name="mapping_type", nullable=false) private String mappingType;
    @Column(name="archived_at") private Instant archivedAt;
    @Version private Integer version;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="created_by") private String createdBy;
}
