package com.company.scopery.modules.trust.accessreview.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.ACCESS_REVIEW_CAMPAIGN) @Getter @Setter @NoArgsConstructor
public class AccessReviewCampaignJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(nullable=false) private String name;
    @Column(name="scope_json", nullable=false, columnDefinition="TEXT") private String scopeJson = "{}";
    @Column(nullable=false) private String status;
    @Column(name="started_at") private Instant startedAt;
    @Column(name="completed_at") private Instant completedAt;
    @Version private Integer version;
}
