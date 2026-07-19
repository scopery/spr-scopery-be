package com.company.scopery.modules.projectnotification.subscription.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = ProjectNotificationTableNames.PROJECT_SUBSCRIPTION)
@Getter @Setter @NoArgsConstructor
public class ProjectNotificationSubscriptionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "subscriber_user_id", nullable = false) private UUID subscriberUserId;
    @Column(name = "workspace_member_id", nullable = false) private UUID workspaceMemberId;
    @Column(name = "subscription_type", nullable = false) private String subscriptionType;
    @Column(nullable = false) private String status;
    @Column(nullable = false) private boolean mandatory;
    @Column(name = "archived_at") private Instant archivedAt;
    @Column(name = "archived_by") private UUID archivedBy;
    @Version private Integer version;
}
