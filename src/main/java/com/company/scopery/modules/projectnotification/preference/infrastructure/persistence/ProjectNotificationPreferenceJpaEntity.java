package com.company.scopery.modules.projectnotification.preference.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = ProjectNotificationTableNames.PREFERENCE)
@Getter @Setter @NoArgsConstructor
public class ProjectNotificationPreferenceJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "task_id") private UUID taskId;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(name = "event_code") private String eventCode;
    @Column(nullable = false) private String channel;
    @Column(nullable = false) private boolean enabled;
    @Column(nullable = false) private boolean muted;
    @Column(name = "mandatory_override", nullable = false) private boolean mandatoryOverride;
    @Version private Integer version;
}
