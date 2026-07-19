package com.company.scopery.modules.resourcecapacity.threshold.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.util.UUID;
@Entity @Table(name = CapacityTableNames.RESOURCE_UTILIZATION_THRESHOLD) @Getter @Setter @NoArgsConstructor
public class UtilizationThresholdPolicyJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="under_allocated_percent", nullable=false) private BigDecimal underAllocatedPercent;
    @Column(name="healthy_min_percent", nullable=false) private BigDecimal healthyMinPercent;
    @Column(name="healthy_max_percent", nullable=false) private BigDecimal healthyMaxPercent;
    @Column(name="watch_max_percent", nullable=false) private BigDecimal watchMaxPercent;
    @Column(name="overloaded_percent", nullable=false) private BigDecimal overloadedPercent;
    @Column(name="critical_overload_percent", nullable=false) private BigDecimal criticalOverloadPercent;
    @Column(nullable=false) private boolean enabled;
    @Version private Integer version;
}
