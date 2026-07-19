package com.company.scopery.modules.profitability.revenueforecast.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = ProfitabilityTableNames.REVENUE_FORECAST)
@Getter
@Setter
@NoArgsConstructor
public class ProfitRevenueForecastJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "profitability_profile_id", nullable = false)
    private UUID profitabilityProfileId;
    @Column(name = "forecast_type", nullable = false, length = 50)
    private String forecastType;
    @Column(nullable = false, length = 10)
    private String currency;
    @Column(name = "forecast_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal forecastAmount;
    @Column(name = "confidence_percent", precision = 5, scale = 2)
    private BigDecimal confidencePercent;
    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;
    @Column(name = "assumption_notes", columnDefinition = "TEXT")
    private String assumptionNotes;
    @Column(name = "generated_by", nullable = false, length = 50)
    private String generatedBy;
    @Column(nullable = false, length = 50)
    private String status;
    @Version
    private Integer version;
}
