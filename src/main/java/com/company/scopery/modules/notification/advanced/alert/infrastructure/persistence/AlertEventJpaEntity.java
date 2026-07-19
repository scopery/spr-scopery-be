package com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = AdvancedNotificationTableNames.ALERT_EVENT) @Getter @Setter @NoArgsConstructor
public class AlertEventJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="alert_rule_id") private UUID alertRuleId;
    @Column(name="source_type") private String sourceType;
    @Column(name="source_id") private UUID sourceId;
    private String severity;
    private String title;
    @Column(nullable=false) private String status;
    @Column(name="dedup_key") private String dedupKey;
    @Column(name="acknowledged_at") private Instant acknowledgedAt;
    @Column(name="dismissed_at") private Instant dismissedAt;
    @Version private Integer version;
}
