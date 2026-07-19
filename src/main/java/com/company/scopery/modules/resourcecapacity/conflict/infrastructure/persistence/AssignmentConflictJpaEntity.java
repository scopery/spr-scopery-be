package com.company.scopery.modules.resourcecapacity.conflict.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CapacityTableNames.RESOURCE_ASSIGNMENT_CONFLICT) @Getter @Setter @NoArgsConstructor
public class AssignmentConflictJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="resource_profile_id") private UUID resourceProfileId;
    @Column(name="conflict_type", nullable=false) private String conflictType;
    @Column(nullable=false) private String severity;
    private String description;
    @Column(nullable=false) private String status;
    @Column(name="detected_at", nullable=false) private Instant detectedAt;
    @Column(name="acknowledged_at") private Instant acknowledgedAt;
    @Column(name="resolved_at") private Instant resolvedAt;
    @Version private Integer version;
}
