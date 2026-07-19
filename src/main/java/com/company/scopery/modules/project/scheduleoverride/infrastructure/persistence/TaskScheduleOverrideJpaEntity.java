package com.company.scopery.modules.project.scheduleoverride.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = ProjectTableNames.TASK_SCHEDULE_OVERRIDE,
        indexes = {
                @Index(name = "idx_project_task_schedule_override_project", columnList = "project_id"),
                @Index(name = "idx_project_task_schedule_override_task", columnList = "task_id"),
                @Index(name = "idx_project_task_schedule_override_status", columnList = "status")
        }
)
public class TaskScheduleOverrideJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "task_id", nullable = false, updatable = false)
    private UUID taskId;

    @Column(name = "override_type", nullable = false, length = 50)
    private String overrideType;

    @Column(name = "manual_start_date")
    private LocalDate manualStartDate;

    @Column(name = "manual_finish_date")
    private LocalDate manualFinishDate;

    @Column(name = "manual_due_date")
    private LocalDate manualDueDate;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancelled_by")
    private UUID cancelledBy;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
