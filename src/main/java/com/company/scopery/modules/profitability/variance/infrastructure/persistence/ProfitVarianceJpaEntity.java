package com.company.scopery.modules.profitability.variance.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = ProfitabilityTableNames.VARIANCE)
@Getter
@Setter
@NoArgsConstructor
public class ProfitVarianceJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "profitability_profile_id", nullable = false)
    private UUID profitabilityProfileId;
    @Column(name = "variance_type", nullable = false, length = 100)
    private String varianceType;
    @Column(name = "from_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal fromAmount;
    @Column(name = "to_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal toAmount;
    @Column(name = "variance_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal varianceAmount;
    @Column(name = "variance_percent", precision = 9, scale = 4)
    private BigDecimal variancePercent;
    @Column(nullable = false, length = 10)
    private String currency;
    @Column(columnDefinition = "TEXT")
    private String explanation;
    @Column(name = "source_snapshot_id")
    private UUID sourceSnapshotId;
    @Version
    private Integer version;
}
