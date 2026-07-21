package com.company.scopery.modules.project.scheduling.schedulerun.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = ProjectTableNames.SCHEDULE_RUN) @Getter @Setter @NoArgsConstructor
public class ScheduleRunJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(nullable=false) private String status;
    @Column(name="algorithm_version", nullable=false) private String algorithmVersion;
    @Column(name="planning_start_date", nullable=false) private LocalDate planningStartDate;
    @Column(name="planning_end_date", nullable=false) private LocalDate planningEndDate;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="input_summary_json", columnDefinition="jsonb") private String inputSummaryJson;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="result_summary_json", columnDefinition="jsonb") private String resultSummaryJson;
    @Column(name="error_code") private String errorCode;
    @Column(name="error_message") private String errorMessage;
    @Column(name="started_at") private Instant startedAt;
    @Column(name="completed_at") private Instant completedAt;
    @Column(name="actor_user_id") private UUID actorUserId;
    @Column(name="trace_id") private String traceId;
    @Version private Integer version;
}
