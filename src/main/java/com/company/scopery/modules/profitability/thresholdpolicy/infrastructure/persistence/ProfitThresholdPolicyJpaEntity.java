package com.company.scopery.modules.profitability.thresholdpolicy.infrastructure.persistence;

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
@Table(name = ProfitabilityTableNames.THRESHOLD_POLICY)
@Getter
@Setter
@NoArgsConstructor
public class ProfitThresholdPolicyJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "healthy_margin_percent", nullable = false, precision = 20, scale = 4)
    private BigDecimal healthyMarginPercent;

    @Column(name = "watch_margin_percent", nullable = false, precision = 20, scale = 4)
    private BigDecimal watchMarginPercent;

    @Column(name = "at_risk_margin_percent", nullable = false, precision = 20, scale = 4)
    private BigDecimal atRiskMarginPercent;

    @Column(name = "loss_risk_margin_percent", nullable = false, precision = 20, scale = 4)
    private BigDecimal lossRiskMarginPercent;

    @Version
    private Integer version;
}
