package com.company.scopery.modules.profitability.threshold.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.util.UUID;
@Entity @Table(name = ProfitabilityTableNames.THRESHOLD) @Getter @Setter @NoArgsConstructor
public class ProfitThresholdPolicyJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="healthy_margin_percent") private BigDecimal healthyMarginPercent;
    @Column(name="watch_margin_percent") private BigDecimal watchMarginPercent;
    @Column(name="at_risk_margin_percent") private BigDecimal atRiskMarginPercent;
    @Column(name="loss_risk_margin_percent") private BigDecimal lossRiskMarginPercent;
    @Version private Integer version;
}
