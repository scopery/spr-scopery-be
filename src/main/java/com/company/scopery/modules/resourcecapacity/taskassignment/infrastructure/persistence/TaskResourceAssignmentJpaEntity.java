package com.company.scopery.modules.resourcecapacity.taskassignment.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name = CapacityTableNames.RESOURCE_TASK_ASSIGNMENT) @Getter @Setter @NoArgsConstructor
public class TaskResourceAssignmentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="task_id", nullable=false) private UUID taskId;
    @Column(name="resource_profile_id", nullable=false) private UUID resourceProfileId;
    @Column(name="assignment_type", nullable=false) private String assignmentType;
    @Column(name="planned_effort_hours") private BigDecimal plannedEffortHours;
    @Column(name="start_date") private LocalDate startDate;
    @Column(name="end_date") private LocalDate endDate;
    @Column(nullable=false) private String status;
    private String notes;
    @Column(name="removed_at") private Instant removedAt;
    @Column(name="removed_by") private UUID removedBy;
    @Version private Integer version;
}
