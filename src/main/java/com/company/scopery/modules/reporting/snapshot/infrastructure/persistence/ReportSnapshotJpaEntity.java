package com.company.scopery.modules.reporting.snapshot.infrastructure.persistence;
import com.company.scopery.modules.reporting.shared.constant.ReportingTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ReportingTableNames.SNAPSHOT) @Getter @Setter @NoArgsConstructor
public class ReportSnapshotJpaEntity {
    @Id private UUID id;
    @Column(name = "report_run_id", nullable = false) private UUID reportRunId;
    @Column(name = "report_definition_id", nullable = false) private UUID reportDefinitionId;
    @Column(name = "workspace_id") private UUID workspaceId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "actor_user_id", nullable = false) private UUID actorUserId;
    @Column(name = "snapshot_type", nullable = false) private String snapshotType;
    @Column(name = "data_json", nullable = false, columnDefinition = "text") private String dataJson;
    @Column(name = "summary_json", columnDefinition = "text") private String summaryJson;
    @Column(name = "masking_summary_json", columnDefinition = "text") private String maskingSummaryJson;
    @Column(name = "generated_at", nullable = false) private Instant generatedAt;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column private Integer version;
}
