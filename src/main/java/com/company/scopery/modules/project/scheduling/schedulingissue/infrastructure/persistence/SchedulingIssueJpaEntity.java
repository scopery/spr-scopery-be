package com.company.scopery.modules.project.scheduling.schedulingissue.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.project.shared.constant.ProjectTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name=ProjectTableNames.SCHEDULING_ISSUE) @Getter @Setter @NoArgsConstructor
public class SchedulingIssueJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="schedule_run_id",nullable=false) private UUID scheduleRunId;
    @Column(name="project_id",nullable=false) private UUID projectId;
    @Column(name="task_id") private UUID taskId;
    @Column(name="user_id") private UUID userId;
    @Column(name="workspace_member_id") private UUID workspaceMemberId;
    @Column(name="issue_type",nullable=false) private String issueType;
    @Column(nullable=false) private String severity;
    @Column(nullable=false) private String message;
    @Column(name="issue_date") private LocalDate issueDate;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="details_json",columnDefinition="jsonb") private String detailsJson;
}
