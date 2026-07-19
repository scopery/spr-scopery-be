package com.company.scopery.modules.scope.review.infrastructure.persistence;
import com.company.scopery.modules.scope.shared.constant.ScopeTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ScopeTableNames.REVIEW) @Getter @Setter @NoArgsConstructor
public class DeliverableReviewJpaEntity {
    @Id private UUID id;
    @Column(name="deliverable_id", nullable=false) private UUID deliverableId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(nullable=false) private String status;
    private String decision;
    @Column(name="reviewer_user_id") private UUID reviewerUserId;
    @Column(columnDefinition="text") private String reason;
    @Column(name="decided_at") private Instant decidedAt;
    @Version private Integer version;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="created_by") private String createdBy;
}
