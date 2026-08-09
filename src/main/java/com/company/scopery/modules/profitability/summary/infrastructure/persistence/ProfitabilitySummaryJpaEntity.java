package com.company.scopery.modules.profitability.summary.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = ProfitabilityTableNames.SUMMARY)
@Getter
@Setter
@NoArgsConstructor
public class ProfitabilitySummaryJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "total_revenue", nullable = false, precision = 20, scale = 4)
    private BigDecimal totalRevenue;

    @Column(name = "total_cost", nullable = false, precision = 20, scale = 4)
    private BigDecimal totalCost;

    @Column(name = "gross_margin", nullable = false, precision = 20, scale = 4)
    private BigDecimal grossMargin;

    @Column(name = "gross_margin_percent", nullable = false, precision = 20, scale = 4)
    private BigDecimal grossMarginPercent;

    @Column(name = "profit_before_tax", nullable = false, precision = 20, scale = 4)
    private BigDecimal profitBeforeTax;

    @Column(name = "pbt_percent", nullable = false, precision = 20, scale = 4)
    private BigDecimal pbtPercent;

    @Column(name = "health_status", nullable = false, length = 50)
    private String healthStatus;

    @Version
    private Integer version;
}
