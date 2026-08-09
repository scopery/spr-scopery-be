package com.company.scopery.modules.project.taskrolecontribution.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = ProjectTableNames.TASK_ROLE_CONTRIBUTION)
@Getter @Setter @NoArgsConstructor
public class TaskRoleContributionJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "cost_role_code", length = 100)
    private String costRoleCode;

    @Column(name = "cost_role_name")
    private String costRoleName;

    @Column(name = "planned_hours", precision = 10, scale = 2)
    private BigDecimal plannedHours;

    @Column(name = "actual_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualHours;

    @Column(name = "rate_snapshot_per_hour", precision = 14, scale = 4)
    private BigDecimal rateSnapshotPerHour;

    @Column(name = "currency_code", length = 10)
    private String currencyCode;

    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
