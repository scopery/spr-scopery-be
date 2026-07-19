package com.company.scopery.modules.projectnotification.reminder.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = ProjectNotificationTableNames.REMINDER_RUN)
@Getter @Setter @NoArgsConstructor
public class ProjectReminderRunJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id") private UUID workspaceId;
    @Column(name = "run_type", nullable = false) private String runType;
    @Column(nullable = false) private String status;
    @Column(name = "started_at", nullable = false) private Instant startedAt;
    @Column(name = "completed_at") private Instant completedAt;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "result_summary_json", columnDefinition = "jsonb") private String resultSummaryJson;
    @Column(name = "error_code") private String errorCode;
    @Column(name = "error_message", columnDefinition = "text") private String errorMessage;
    @Column(name = "trace_id") private String traceId;
}
