package com.company.scopery.modules.trust.accessreview.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.PERMISSION_REVIEW_FINDING) @Getter @Setter @NoArgsConstructor
public class PermissionReviewFindingJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="campaign_id") private UUID campaignId;
    @Column(name="finding_type", nullable=false) private String findingType;
    @Column(nullable=false) private String severity;
    private String recommendation;
    @Column(nullable=false) private String status;
    @Column(name="resolved_at") private Instant resolvedAt;
    @Version private Integer version;
}
