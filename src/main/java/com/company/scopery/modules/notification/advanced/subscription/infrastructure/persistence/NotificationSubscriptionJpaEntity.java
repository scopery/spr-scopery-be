package com.company.scopery.modules.notification.advanced.subscription.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = AdvancedNotificationTableNames.SUBSCRIPTION) @Getter @Setter @NoArgsConstructor
public class NotificationSubscriptionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="user_id", nullable=false) private UUID userId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="subscription_level", nullable=false) private String subscriptionLevel;
    @Column(name="auto_subscribed", nullable=false) private boolean autoSubscribed;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
