package com.company.scopery.modules.notification.advanced.preference.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = AdvancedNotificationTableNames.PREFERENCE) @Getter @Setter @NoArgsConstructor
public class NotificationPreferenceProfileJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="user_id", nullable=false) private UUID userId;
    private String timezone;
    @Column(name="default_mode") private String defaultMode;
    @Column(name="digest_enabled", nullable=false) private boolean digestEnabled;
    @Column(name="quiet_hours_enabled", nullable=false) private boolean quietHoursEnabled;
    @Column(name="quiet_hours_start") private String quietHoursStart;
    @Column(name="quiet_hours_end") private String quietHoursEnd;
    @Version private Integer version;
}
