package com.company.scopery.modules.project.project.infrastructure.persistence;

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
        name = ProjectTableNames.PROJECT,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_project_project_workspace_code",
                columnNames = {"workspace_id", "code"}
        ),
        indexes = {
                @Index(name = "idx_project_project_workspace_id", columnList = "workspace_id"),
                @Index(name = "idx_project_project_organization_id", columnList = "organization_id"),
                @Index(name = "idx_project_project_status", columnList = "status"),
                @Index(name = "idx_project_project_code", columnList = "code")
        }
)
public class ProjectJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "owner_user_id")
    private UUID ownerUserId;

    @Column(name = "default_currency", length = 10)
    private String defaultCurrency;

    @Column(name = "planned_start_date")
    private LocalDate plannedStartDate;

    @Column(name = "planned_end_date")
    private LocalDate plannedEndDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "activated_by")
    private UUID activatedBy;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "archived_by")
    private UUID archivedBy;

    @Column(name = "source_template_id")
    private UUID sourceTemplateId;

    @Column(name = "source_template_version_id")
    private UUID sourceTemplateVersionId;

    @Column(name = "source_template_applied_at")
    private Instant sourceTemplateAppliedAt;

    @Column(name = "current_schedule_run_id")
    private UUID currentScheduleRunId;

    @Column(name = "current_estimation_run_id")
    private UUID currentEstimationRunId;

    @Column(name = "current_finance_scenario_id")
    private UUID currentFinanceScenarioId;

    @Column(name = "current_quote_id")
    private UUID currentQuoteId;

    @Column(name = "current_quote_version_id")
    private UUID currentQuoteVersionId;

    @Column(name = "current_baseline_id")
    private UUID currentBaselineId;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
