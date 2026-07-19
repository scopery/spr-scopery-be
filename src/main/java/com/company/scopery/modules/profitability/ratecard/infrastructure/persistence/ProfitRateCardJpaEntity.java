package com.company.scopery.modules.profitability.ratecard.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = ProfitabilityTableNames.RATE_CARD)
@Getter
@Setter
@NoArgsConstructor
public class ProfitRateCardJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "project_id")
    private UUID projectId;
    @Column(name = "rate_code", nullable = false, length = 150)
    private String rateCode;
    @Column(nullable = false, length = 255)
    private String name;
    @Column(name = "rate_type", nullable = false, length = 50)
    private String rateType;
    @Column(name = "role_name")
    private String roleName;
    @Column(name = "team_id")
    private UUID teamId;
    @Column(nullable = false, length = 10)
    private String currency;
    @Column(name = "amount_per_hour", precision = 19, scale = 4)
    private BigDecimal amountPerHour;
    @Column(name = "amount_per_day", precision = 19, scale = 4)
    private BigDecimal amountPerDay;
    @Column(nullable = false, length = 50)
    private String status;
    @Version
    private Integer version;
}
