package com.company.scopery.modules.project.task.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = ProjectTableNames.TASK,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_project_task_project_code",
                columnNames = {"project_id", "code"}
        ),
        indexes = {
                @Index(name = "idx_project_task_project_id", columnList = "project_id"),
                @Index(name = "idx_project_task_project_phase_id", columnList = "project_phase_id"),
                @Index(name = "idx_project_task_wbs_node_id", columnList = "wbs_node_id"),
                @Index(name = "idx_project_task_status", columnList = "status"),
                @Index(name = "idx_project_task_priority", columnList = "priority")
        }
)
public class TaskJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "project_phase_id", nullable = false)
    private UUID projectPhaseId;

    @Column(name = "wbs_node_id")
    private UUID wbsNodeId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "in_charge_user_id")
    private UUID inChargeUserId;

    @Column(name = "planned_role_code", length = 100)
    private String plannedRoleCode;

    @Column(name = "planned_role_name", length = 255)
    private String plannedRoleName;

    @Column(name = "estimate_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimateHours;

    @Column(name = "planned_start_date")
    private LocalDate plannedStartDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "priority", nullable = false, length = 50)
    private String priority;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "started_by")
    private UUID startedBy;

    @Column(name = "blocked_at")
    private Instant blockedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "completed_by")
    private UUID completedBy;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancelled_by")
    private UUID cancelledBy;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "archived_by")
    private UUID archivedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
