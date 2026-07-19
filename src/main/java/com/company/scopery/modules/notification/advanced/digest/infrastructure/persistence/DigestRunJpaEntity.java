package com.company.scopery.modules.notification.advanced.digest.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = AdvancedNotificationTableNames.DIGEST_RUN) @Getter @Setter @NoArgsConstructor
public class DigestRunJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="digest_rule_id") private UUID digestRuleId;
    @Column(name="recipient_user_id") private UUID recipientUserId;
    @Column(nullable=false) private String status;
    @Column(name="notification_count", nullable=false) private int notificationCount;
    @Column(name="sent_at") private Instant sentAt;
    @Version private Integer version;
}
