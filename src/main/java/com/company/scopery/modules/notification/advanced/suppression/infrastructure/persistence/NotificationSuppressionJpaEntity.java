package com.company.scopery.modules.notification.advanced.suppression.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = AdvancedNotificationTableNames.SUPPRESSION_LEDGER) @Getter @Setter @NoArgsConstructor
public class NotificationSuppressionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="user_id") private UUID userId;
    @Column(name="category_code") private String categoryCode;
    @Column(name="channel_code") private String channelCode;
    @Column(name="reason_code", nullable=false) private String reasonCode;
    @Column(name="source_type") private String sourceType;
    @Column(name="source_id") private UUID sourceId;
    @Column(name="suppressed_at", nullable=false) private Instant suppressedAt;
    @Column(name="expires_at") private Instant expiresAt;
    @Version private Integer version;
}
