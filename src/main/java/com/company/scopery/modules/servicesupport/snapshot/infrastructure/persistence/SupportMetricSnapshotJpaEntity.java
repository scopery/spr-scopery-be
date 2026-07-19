package com.company.scopery.modules.servicesupport.snapshot.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal; import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name = SupportTableNames.METRIC_SNAPSHOT)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class SupportMetricSnapshotJpaEntity implements Persistable<UUID> {
    @jakarta.persistence.Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "service_profile_id") private UUID serviceProfileId;
    @Column(name = "period_start") private LocalDate periodStart;
    @Column(name = "period_end") private LocalDate periodEnd;
    @Column(name = "open_cases", nullable = false) private int openCases;
    @Column(name = "new_cases", nullable = false) private int newCases;
    @Column(name = "resolved_cases", nullable = false) private int resolvedCases;
    @Column(name = "closed_cases", nullable = false) private int closedCases;
    @Column(name = "sla_at_risk", nullable = false) private int slaAtRisk;
    @Column(name = "sla_breached", nullable = false) private int slaBreached;
    @Column(name = "critical_incidents", nullable = false) private int criticalIncidents;
    @Column(name = "avg_first_response_minutes") private BigDecimal avgFirstResponseMinutes;
    @Column(name = "avg_resolution_minutes") private BigDecimal avgResolutionMinutes;
    @Column(name = "maintenance_windows_planned", nullable = false) private int maintenanceWindowsPlanned;
    @Column(name = "support_effort_hours", nullable = false) private BigDecimal supportEffortHours;
    @Column(name = "support_cost_input") private BigDecimal supportCostInput;
    @Column private String currency;
    @Column(name = "snapshot_source", nullable = false) private String snapshotSource;
    @Column(name = "snapshot_at", nullable = false) private Instant snapshotAt;
    @Version private Integer version;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Override @Transient public boolean isNew() { return createdAt == null; }
}
