package com.company.scopery.modules.resourcecapacity.workload.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CapacityTableNames.RESOURCE_WORKLOAD_SNAPSHOT) @Getter @Setter @NoArgsConstructor
public class WorkloadSnapshotJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="total_capacity_hours", nullable=false) private BigDecimal totalCapacityHours = BigDecimal.ZERO;
    @Column(name="total_allocated_hours", nullable=false) private BigDecimal totalAllocatedHours = BigDecimal.ZERO;
    @Column(name="total_estimated_effort_hours", nullable=false) private BigDecimal totalEstimatedEffortHours = BigDecimal.ZERO;
    @Column(name="total_forecast_effort_hours", nullable=false) private BigDecimal totalForecastEffortHours = BigDecimal.ZERO;
    @Column(name="total_actual_observed_effort_hours", nullable=false) private BigDecimal totalActualObservedEffortHours = BigDecimal.ZERO;
    @Column(name="capacity_gap_hours", nullable=false) private BigDecimal capacityGapHours = BigDecimal.ZERO;
    @Column(name="overload_count", nullable=false) private int overloadCount;
    @Column(name="understaffed_role_count", nullable=false) private int understaffedRoleCount;
    @Column(name="cost_forecast_input") private BigDecimal costForecastInput;
    @Column(name="snapshot_source", nullable=false) private String snapshotSource;
    @Column(name="snapshot_at", nullable=false) private Instant snapshotAt;
    @Version private Integer version;
}
