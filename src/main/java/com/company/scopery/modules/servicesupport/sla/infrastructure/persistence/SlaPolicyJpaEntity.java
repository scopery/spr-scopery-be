package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = SupportTableNames.SLA_POLICY) @Getter @Setter @NoArgsConstructor
public class SlaPolicyJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="policy_code", nullable=false) private String policyCode;
    @Column(nullable=false) private String name;
    @Column(name="first_response_minutes") private Integer firstResponseMinutes;
    @Column(name="resolve_minutes") private Integer resolveMinutes;
    @Column(name="business_hours_only", nullable=false) private Boolean businessHoursOnly = true;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
