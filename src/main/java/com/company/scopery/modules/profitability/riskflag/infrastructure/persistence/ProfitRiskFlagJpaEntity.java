package com.company.scopery.modules.profitability.riskflag.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = ProfitabilityTableNames.RISK_FLAG)
@Getter
@Setter
@NoArgsConstructor
public class ProfitRiskFlagJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(nullable = false, length = 500)
    private String reason;
    @Column(name = "impact_type", nullable = false, length = 50)
    private String impactType;
    @Column(name = "amount_at_risk", precision = 19, scale = 4)
    private BigDecimal amountAtRisk;
    @Column(nullable = false, length = 50)
    private String status;
    @Version
    private Integer version;
}
