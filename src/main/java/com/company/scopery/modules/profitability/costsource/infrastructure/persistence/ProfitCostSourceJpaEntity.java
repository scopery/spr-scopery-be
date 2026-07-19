package com.company.scopery.modules.profitability.costsource.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = ProfitabilityTableNames.COST_SOURCE)
@Getter
@Setter
@NoArgsConstructor
public class ProfitCostSourceJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "profile_id", nullable = false)
    private UUID profileId;
    @Column(name = "source_type", nullable = false)
    private String sourceType;
    @Column(name = "source_id")
    private UUID sourceId;
    @Column(name = "effort_hours")
    private BigDecimal effortHours;
    @Column(name = "rate_amount")
    private BigDecimal rateAmount;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String currency;
    @Column(name = "included_in_forecast", nullable = false)
    private boolean includedInForecast;
    @Column(nullable = false)
    private String status;
    @Version
    private Integer version;
}
