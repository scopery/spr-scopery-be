package com.company.scopery.modules.projectbaseline.changerequest.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;

@Entity @Table(name = ProjectBaselineTableNames.CHANGE_REQUEST)
@Getter @Setter @NoArgsConstructor
public class ChangeRequestJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="baseline_id") private UUID baselineId;
    @Column(nullable=false) private String code;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(name="change_type", nullable=false) private String changeType;
    private String priority;
    @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String reason;
    @Column(name="requested_by") private UUID requestedBy;
    @Column(name="requested_at") private Instant requestedAt;
    @Column(name="submitted_at") private Instant submittedAt;
    @Column(name="submitted_by") private UUID submittedBy;
    @Column(name="approved_at") private Instant approvedAt;
    @Column(name="approved_by") private UUID approvedBy;
    @Column(name="rejected_at") private Instant rejectedAt;
    @Column(name="rejected_by") private UUID rejectedBy;
    @Column(name="rejection_reason", columnDefinition="text") private String rejectionReason;
    @Column(name="cancelled_at") private Instant cancelledAt;
    @Column(name="cancelled_by") private UUID cancelledBy;
    @Column(name="applied_at") private Instant appliedAt;
    @Column(name="applied_by") private UUID appliedBy;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Column(name="trace_id") private String traceId;
    @Version private Integer version;
}
