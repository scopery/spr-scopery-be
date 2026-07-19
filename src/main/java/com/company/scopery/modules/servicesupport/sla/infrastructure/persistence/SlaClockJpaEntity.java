package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = SupportTableNames.SLA_CLOCK) @Getter @Setter @NoArgsConstructor
public class SlaClockJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="support_case_id", nullable=false) private UUID supportCaseId;
    @Column(name="sla_policy_id", nullable=false) private UUID slaPolicyId;
    @Column(name="clock_type", nullable=false) private String clockType;
    @Column(name="started_at", nullable=false) private Instant startedAt;
    @Column(name="due_at") private Instant dueAt;
    @Column(name="paused_at") private Instant pausedAt;
    @Column(name="completed_at") private Instant completedAt;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
