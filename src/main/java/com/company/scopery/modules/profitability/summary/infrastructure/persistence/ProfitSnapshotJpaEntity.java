package com.company.scopery.modules.profitability.summary.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.util.UUID;
@Entity @Table(name = ProfitabilityTableNames.SNAPSHOT) @Getter @Setter @NoArgsConstructor
public class ProfitSnapshotJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="profile_id", nullable=false) private UUID profileId;
    @Column(name="baseline_revenue") private BigDecimal baselineRevenue;
    @Column(name="forecast_revenue") private BigDecimal forecastRevenue;
    @Column(name="baseline_cost") private BigDecimal baselineCost;
    @Column(name="forecast_cost") private BigDecimal forecastCost;
    @Column(name="forecast_profit") private BigDecimal forecastProfit;
    @Column(name="forecast_margin_percent") private BigDecimal forecastMarginPercent;
    @Column(name="profitability_status") private String profitabilityStatus;
    @Version private Integer version;
}
