package com.company.scopery.modules.projectbaseline.baseline.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = ProjectBaselineTableNames.PROJECT_BASELINE)
@Getter @Setter @NoArgsConstructor
public class ProjectBaselineJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "baseline_number", nullable = false) private Integer baselineNumber;
    @Column(nullable = false) private String name;
    @Column(columnDefinition = "text") private String description;
    @Column(nullable = false) private String status;
    @Column(name = "current_flag", nullable = false) private boolean currentFlag;
    @Column(name = "source_schedule_run_id") private UUID sourceScheduleRunId;
    @Column(name = "source_estimation_run_id") private UUID sourceEstimationRunId;
    @Column(name = "source_finance_scenario_id") private UUID sourceFinanceScenarioId;
    @Column(name = "source_quote_version_id") private UUID sourceQuoteVersionId;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "snapshot_json", nullable = false, columnDefinition = "jsonb") private String snapshotJson;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "summary_json", nullable = false, columnDefinition = "jsonb") private String summaryJson;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "validation_json", columnDefinition = "jsonb") private String validationJson;
    @Column(name = "formula_version", nullable = false) private String formulaVersion;
    @Column(name = "approved_at") private Instant approvedAt;
    @Column(name = "approved_by") private UUID approvedBy;
    @Column(name = "archived_at") private Instant archivedAt;
    @Column(name = "archived_by") private UUID archivedBy;
    @Column(name = "trace_id") private String traceId;
    @Version private Integer version;
}
