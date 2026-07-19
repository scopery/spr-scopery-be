package com.company.scopery.modules.projectnotification.reminder.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = ProjectNotificationTableNames.REMINDER_EMISSION)
@Getter @Setter @NoArgsConstructor
public class ProjectReminderEmissionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "reminder_run_id") private UUID reminderRunId;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "task_id") private UUID taskId;
    @Column(name = "milestone_id") private UUID milestoneId;
    @Column(name = "recipient_user_id", nullable = false) private UUID recipientUserId;
    @Column(name = "reminder_type", nullable = false) private String reminderType;
    @Column(name = "reminder_date", nullable = false) private LocalDate reminderDate;
    @Column(name = "dedup_key", nullable = false, unique = true) private String dedupKey;
    @Column(nullable = false) private String status;
}
