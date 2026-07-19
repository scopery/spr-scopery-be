package com.company.scopery.modules.aiplanning.reviewaction.infrastructure.persistence;

import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.util.UUID;

@Entity @Table(name = AiPlanningTableNames.REVIEW_ACTION) @Getter @Setter @NoArgsConstructor
public class AiPlanningReviewActionJpaEntity {
    @Id private UUID id;
    @Column(name = "suggestion_id", nullable = false) private UUID suggestionId;
    @Column(name = "suggestion_item_id") private UUID suggestionItemId;
    @Column(nullable = false) private String action;
    @Column(name = "actor_user_id", nullable = false) private UUID actorUserId;
    @Column(columnDefinition = "text") private String reason;
    @Column(name = "trace_id") private String traceId;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
}
