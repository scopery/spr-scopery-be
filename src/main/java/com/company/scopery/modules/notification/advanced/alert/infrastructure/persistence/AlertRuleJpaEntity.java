package com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = AdvancedNotificationTableNames.ALERT_RULE) @Getter @Setter @NoArgsConstructor
public class AlertRuleJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="rule_code", nullable=false) private String ruleCode;
    @Column(nullable=false) private String name;
    private String category;
    @Column(name="condition_json") private String conditionJson;
    @Column(name="bypass_quiet_hours", nullable=false) private boolean bypassQuietHours;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
