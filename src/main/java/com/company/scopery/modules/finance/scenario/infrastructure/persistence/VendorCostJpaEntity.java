package com.company.scopery.modules.finance.scenario.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.finance.shared.constant.FinanceTableNames;
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
@Table(name = FinanceTableNames.FINANCE_VENDOR_COST)
@Getter
@Setter
@NoArgsConstructor
public class VendorCostJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "finance_scenario_id", nullable = false)
    private UUID financeScenarioId;

    @Column(name = "project_phase_id")
    private UUID projectPhaseId;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, precision = 20, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;

    @Column(nullable = false, length = 50)
    private String status;

    @Version
    private Integer version;
}
