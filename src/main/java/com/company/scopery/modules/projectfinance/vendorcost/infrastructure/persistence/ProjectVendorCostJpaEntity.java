package com.company.scopery.modules.projectfinance.vendorcost.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectfinance.shared.constant.ProjectFinanceTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = ProjectFinanceTableNames.VENDOR_COST)
@Getter
@Setter
@NoArgsConstructor
public class ProjectVendorCostJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "finance_scenario_id", nullable = false)
    private UUID financeScenarioId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "project_phase_id")
    private UUID projectPhaseId;
    @Column(name = "vendor_name")
    private String vendorName;
    @Column(name = "external_party_id")
    private UUID externalPartyId;
    @Column(nullable = false, columnDefinition = "text")
    private String description;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;
    @Column(nullable = false)
    private String status;
    @Column(name = "archived_at")
    private Instant archivedAt;
    @Column(name = "archived_by")
    private UUID archivedBy;
    @Version
    private Integer version;
}
