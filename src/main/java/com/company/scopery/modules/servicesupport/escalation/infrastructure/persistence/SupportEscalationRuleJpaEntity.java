package com.company.scopery.modules.servicesupport.escalation.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = SupportTableNames.ESCALATION_RULE) @Getter @Setter @NoArgsConstructor
public class SupportEscalationRuleJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "service_profile_id") private UUID serviceProfileId;
    @Column(name = "queue_id") private UUID queueId;
    @Column(name = "rule_code", nullable = false) private String ruleCode;
    @Column(nullable = false) private String name;
    @Column(name = "trigger_type", nullable = false) private String triggerType;
    @Column(nullable = false) private boolean enabled;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}
