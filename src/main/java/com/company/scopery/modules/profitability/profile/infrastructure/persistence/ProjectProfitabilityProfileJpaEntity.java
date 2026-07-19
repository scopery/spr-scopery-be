package com.company.scopery.modules.profitability.profile.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ProfitabilityTableNames.PROFILE) @Getter @Setter @NoArgsConstructor
public class ProjectProfitabilityProfileJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(nullable=false) private String currency;
    @Column(name="tracking_mode") private String trackingMode;
    @Column(name="revenue_mode") private String revenueMode;
    @Column(name="cost_mode") private String costMode;
    @Column(name="owner_user_id") private UUID ownerUserId;
    @Column(name="portal_visibility") private String portalVisibility;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
