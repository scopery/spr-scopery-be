package com.company.scopery.modules.project.scheduling.scheduleddailywork.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name=ProjectTableNames.SCHEDULED_DAILY_WORK) @Getter @Setter @NoArgsConstructor
public class ScheduledDailyWorkJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="schedule_run_id",nullable=false) private UUID scheduleRunId;
    @Column(name="task_schedule_id",nullable=false) private UUID taskScheduleId;
    @Column(name="project_id",nullable=false) private UUID projectId;
    @Column(name="task_id",nullable=false) private UUID taskId;
    @Column(name="workspace_member_id",nullable=false) private UUID workspaceMemberId;
    @Column(name="user_id",nullable=false) private UUID userId;
    @Column(name="work_date",nullable=false) private LocalDate workDate;
    @Column(name="planned_hours",nullable=false) private BigDecimal plannedHours;
    @Column(name="capacity_hours",nullable=false) private BigDecimal capacityHours;
    @Column(name="remaining_capacity_after",nullable=false) private BigDecimal remainingCapacityAfter;
}
