package com.company.scopery.modules.notification.advanced.channelpref.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = AdvancedNotificationTableNames.CHANNEL_PREFERENCE) @Getter @Setter @NoArgsConstructor
public class NotificationChannelPreferenceJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="user_id", nullable=false) private UUID userId;
    @Column(name="category_code", nullable=false) private String categoryCode;
    @Column(name="channel_code", nullable=false) private String channelCode;
    @Column(nullable=false) private boolean enabled;
    @Version private Integer version;
}
