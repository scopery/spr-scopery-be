package com.company.scopery.modules.resourcecapacity.risk.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CapacityTableNames.RESOURCE_RISK_FLAG) @Getter @Setter @NoArgsConstructor
public class ResourceRiskFlagJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="resource_profile_id") private UUID resourceProfileId;
    @Column(name="risk_reason", nullable=false) private String riskReason;
    @Column(name="impact_type", nullable=false) private String impactType;
    private String description;
    @Column(nullable=false) private String status;
    @Column(name="mitigated_at") private Instant mitigatedAt;
    @Column(name="closed_at") private Instant closedAt;
    @Version private Integer version;
}
