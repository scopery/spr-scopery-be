package com.company.scopery.modules.clientportal.review.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity; import com.company.scopery.modules.clientportal.shared.constant.ClientPortalTableNames;
import jakarta.persistence.*; import lombok.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name=ClientPortalTableNames.REVIEW_REQUEST) @Getter @Setter @NoArgsConstructor
public class ClientReviewRequestJpaEntity extends AuditableJpaEntity {
    @Id private UUID id; @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="target_type", nullable=false) private String targetType; @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(nullable=false) private String title; @Column(nullable=false) private String status;
    @Column(name="due_at") private Instant dueAt; @Column(name="requested_by") private UUID requestedBy;
    @Column(name="assigned_portal_account_id") private UUID assignedPortalAccountId; @Version private Integer version;
}
