package com.company.scopery.modules.reporting.run.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.reporting.shared.constant.ReportingTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ReportingTableNames.RUN) @Getter @Setter @NoArgsConstructor
public class ReportRunJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "report_definition_id", nullable = false) private UUID reportDefinitionId;
    @Column(name = "workspace_id") private UUID workspaceId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "actor_user_id", nullable = false) private UUID actorUserId;
    @Column(nullable = false) private String status;
    @Column(name = "filters_json", columnDefinition = "text") private String filtersJson;
    @Column(name = "selected_fields_json", columnDefinition = "text") private String selectedFieldsJson;
    @Column(name = "access_summary_json", columnDefinition = "text") private String accessSummaryJson;
    @Column(name = "masking_summary_json", columnDefinition = "text") private String maskingSummaryJson;
    @Column(name = "result_summary_json", columnDefinition = "text") private String resultSummaryJson;
    @Column(name = "error_code") private String errorCode;
    @Column(name = "error_message", columnDefinition = "text") private String errorMessage;
    @Column(name = "started_at") private Instant startedAt;
    @Column(name = "completed_at") private Instant completedAt;
    @Column(name = "trace_id") private String traceId;
    @Version private Integer version;
}
