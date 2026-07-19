package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = SupportTableNames.SLA_BREACH) @Getter @Setter @NoArgsConstructor
public class SlaBreachJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="support_case_id", nullable=false) private UUID supportCaseId;
    @Column(name="sla_clock_id", nullable=false) private UUID slaClockId;
    @Column(name="breach_type", nullable=false) private String breachType;
    @Column(name="breached_at", nullable=false) private Instant breachedAt;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
