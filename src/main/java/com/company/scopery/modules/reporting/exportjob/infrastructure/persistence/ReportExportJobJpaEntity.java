package com.company.scopery.modules.reporting.exportjob.infrastructure.persistence;
import com.company.scopery.modules.reporting.shared.constant.ReportingTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ReportingTableNames.EXPORT_JOB) @Getter @Setter @NoArgsConstructor
public class ReportExportJobJpaEntity {
    @Id private UUID id;
    @Column(name = "report_run_id") private UUID reportRunId;
    @Column(name = "report_snapshot_id") private UUID reportSnapshotId;
    @Column(name = "report_definition_id", nullable = false) private UUID reportDefinitionId;
    @Column(name = "workspace_id") private UUID workspaceId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "actor_user_id", nullable = false) private UUID actorUserId;
    @Column(nullable = false) private String format;
    @Column(nullable = false) private String status;
    @Column(name = "file_name") private String fileName;
    @Column(name = "file_mime_type") private String fileMimeType;
    @Column(name = "file_size_bytes") private Long fileSizeBytes;
    @Column(name = "storage_key") private String storageKey;
    @Column(name = "content_text", columnDefinition = "text") private String contentText;
    @Column(name = "download_expires_at") private Instant downloadExpiresAt;
    @Column(name = "filters_json", columnDefinition = "text") private String filtersJson;
    @Column(name = "selected_fields_json", columnDefinition = "text") private String selectedFieldsJson;
    @Column(name = "masking_summary_json", columnDefinition = "text") private String maskingSummaryJson;
    @Column(name = "error_code") private String errorCode;
    @Column(name = "error_message", columnDefinition = "text") private String errorMessage;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "completed_at") private Instant completedAt;
    @Column(name = "trace_id") private String traceId;
    @Column private Integer version;
}
