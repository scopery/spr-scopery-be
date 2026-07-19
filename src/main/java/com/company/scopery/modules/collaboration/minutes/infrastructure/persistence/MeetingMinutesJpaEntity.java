package com.company.scopery.modules.collaboration.minutes.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CollaborationTableNames.MINUTES) @Getter @Setter @NoArgsConstructor
public class MeetingMinutesJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="meeting_id", nullable=false) private UUID meetingId;
    @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String summary;
    @Column(name="decisions_summary", columnDefinition="text") private String decisionsSummary;
    @Column(name="actions_summary", columnDefinition="text") private String actionsSummary;
    @Column(name="client_visible_summary", columnDefinition="text") private String clientVisibleSummary;
    @Column(name="document_id") private UUID documentId;
    @Column(name="document_version_id") private UUID documentVersionId;
    @Column(name="submitted_at") private Instant submittedAt; @Column(name="submitted_by") private UUID submittedBy;
    @Column(name="approved_at") private Instant approvedAt; @Column(name="approved_by") private UUID approvedBy;
    @Column(name="rejected_at") private Instant rejectedAt; @Column(name="rejected_by") private UUID rejectedBy;
    @Column(name="rejection_reason", columnDefinition="text") private String rejectionReason;
    @Column(name="trace_id") private String traceId;
    @Version private Integer version;
}
