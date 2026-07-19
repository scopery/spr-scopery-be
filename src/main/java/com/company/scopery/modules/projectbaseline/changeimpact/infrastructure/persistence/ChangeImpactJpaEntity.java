package com.company.scopery.modules.projectbaseline.changeimpact.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineTableNames;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal; import java.util.UUID;

@Entity @Table(name = ProjectBaselineTableNames.CHANGE_IMPACT)
@Getter @Setter @NoArgsConstructor
public class ChangeImpactJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="change_request_id", nullable=false, unique=true) private UUID changeRequestId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="currency_code") private String currencyCode;
    @Column(name="scope_impact", columnDefinition="text") private String scopeImpact;
    @Column(name="schedule_impact_days") private Integer scheduleImpactDays;
    @Column(name="estimate_hours_impact") private BigDecimal estimateHoursImpact;
    @Column(name="labor_cost_impact") private BigDecimal laborCostImpact;
    @Column(name="direct_cost_impact") private BigDecimal directCostImpact;
    @Column(name="overhead_impact") private BigDecimal overheadImpact;
    @Column(name="revenue_impact") private BigDecimal revenueImpact;
    @Column(name="gross_margin_impact") private BigDecimal grossMarginImpact;
    @Column(name="pbt_impact") private BigDecimal pbtImpact;
    @Column(name="quote_amount_impact") private BigDecimal quoteAmountImpact;
    @Column(name="risk_impact") private String riskImpact;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="impact_summary_json", columnDefinition="jsonb") private String impactSummaryJson;
    @Version private Integer version;
}
