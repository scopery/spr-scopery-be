package com.company.scopery.modules.clientportal.review.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalTableNames;
import jakarta.persistence.*; import lombok.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ClientPortalTableNames.REVIEW_DECISION) @Getter @Setter @NoArgsConstructor
public class ClientReviewDecisionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "review_request_id", nullable = false) private UUID reviewRequestId;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(nullable = false) private String decision;
    @Column(columnDefinition = "TEXT") private String comment;
    @Column(name = "decided_by_portal_account_id") private UUID decidedByPortalAccountId;
    @Column(name = "decided_at", nullable = false) private Instant decidedAt;
    @Version private Integer version;
}
