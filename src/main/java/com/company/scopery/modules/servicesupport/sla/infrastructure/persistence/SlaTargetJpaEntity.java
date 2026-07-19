package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = SupportTableNames.SLA_TARGET) @Getter @Setter @NoArgsConstructor
public class SlaTargetJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "sla_policy_id", nullable = false) private UUID slaPolicyId;
    @Column(name = "request_type_id") private UUID requestTypeId;
    @Column private String priority;
    @Column(name = "target_type", nullable = false) private String targetType;
    @Column(name = "duration_minutes", nullable = false) private int durationMinutes;
    @Column(nullable = false) private boolean enabled;
    @Version private Integer version;
}
