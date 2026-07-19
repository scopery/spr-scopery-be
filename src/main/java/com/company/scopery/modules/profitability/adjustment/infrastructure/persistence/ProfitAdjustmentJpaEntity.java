package com.company.scopery.modules.profitability.adjustment.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.util.UUID;
@Entity @Table(name = ProfitabilityTableNames.ADJUSTMENT) @Getter @Setter @NoArgsConstructor
public class ProfitAdjustmentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="profile_id", nullable=false) private UUID profileId;
    @Column(name="adjustment_type", nullable=false) private String adjustmentType;
    @Column(nullable=false) private BigDecimal amount;
    @Column(nullable=false) private String reason;
    @Column(nullable=false) private String status;
    @Column(name="source_link_type") private String sourceLinkType;
    @Column(name="source_link_id") private UUID sourceLinkId;
    @Version private Integer version;
}
