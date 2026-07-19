package com.company.scopery.modules.scope.deliverable.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.scope.shared.constant.ScopeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name=ScopeTableNames.DELIVERABLE) @Getter @Setter @NoArgsConstructor
public class DeliverableJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_phase_id") private UUID projectPhaseId;
    @Column(name="scope_item_id") private UUID scopeItemId;
    @Column(nullable=false) private String type;
    private String code;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(name="acceptance_required", nullable=false) private boolean acceptanceRequired;
    @Column(nullable=false) private String status;
    @Column(name="accepted_at") private Instant acceptedAt;
    @Column(name="accepted_by") private UUID acceptedBy;
    @Column(name="rejected_at") private Instant rejectedAt;
    @Column(name="rejected_by") private UUID rejectedBy;
    @Column(name="rejection_reason", columnDefinition="text") private String rejectionReason;
    @Column(name="reopened_at") private Instant reopenedAt;
    @Column(name="reopened_by") private UUID reopenedBy;
    @Column(name="reopen_reason", columnDefinition="text") private String reopenReason;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
