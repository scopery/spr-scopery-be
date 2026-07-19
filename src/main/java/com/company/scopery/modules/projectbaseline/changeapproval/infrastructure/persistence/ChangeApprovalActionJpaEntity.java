package com.company.scopery.modules.projectbaseline.changeapproval.infrastructure.persistence;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant; import java.util.UUID;

@Entity @Table(name = ProjectBaselineTableNames.CHANGE_APPROVAL_ACTION)
@Getter @Setter @NoArgsConstructor
public class ChangeApprovalActionJpaEntity {
    @Id private UUID id;
    @Column(name="change_request_id", nullable=false) private UUID changeRequestId;
    @Column(nullable=false) private String action;
    @Column(name="actor_user_id", nullable=false) private UUID actorUserId;
    @Column(columnDefinition="text") private String reason;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="trace_id") private String traceId;
}
