package com.company.scopery.modules.project.scheduling.taskschedule.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name=ProjectTableNames.TASK_SCHEDULE) @Getter @Setter @NoArgsConstructor
public class TaskScheduleJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="schedule_run_id", nullable=false) private UUID scheduleRunId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="task_id", nullable=false) private UUID taskId;
    @Column(name="assignee_user_id") private UUID assigneeUserId;
    @Column(name="workspace_member_id") private UUID workspaceMemberId;
    @Column(name="estimated_start_date") private LocalDate estimatedStartDate;
    @Column(name="estimated_finish_date") private LocalDate estimatedFinishDate;
    @Column(name="scheduled_hours", nullable=false) private BigDecimal scheduledHours;
    @Column(name="unscheduled_hours", nullable=false) private BigDecimal unscheduledHours;
    @Column(name="due_date") private LocalDate dueDate;
    @Column(name="due_date_capacity_gap_hours", nullable=false) private BigDecimal dueDateCapacityGapHours;
    @Column(name="risk_status", nullable=false) private String riskStatus;
    @Column(name="schedule_status", nullable=false) private String scheduleStatus;
}
