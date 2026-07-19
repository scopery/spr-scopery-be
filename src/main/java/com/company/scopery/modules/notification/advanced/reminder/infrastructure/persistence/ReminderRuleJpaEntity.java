package com.company.scopery.modules.notification.advanced.reminder.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.advanced.shared.constant.AdvancedNotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = AdvancedNotificationTableNames.REMINDER_RULE) @Getter @Setter @NoArgsConstructor
public class ReminderRuleJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="rule_code", nullable=false) private String ruleCode;
    @Column(nullable=false) private String name;
    @Column(name="condition_json") private String conditionJson;
    @Column(name="recipient_rule_json") private String recipientRuleJson;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
