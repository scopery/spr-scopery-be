package com.company.scopery.modules.trust.privacy.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.PRIVACY_REQUEST) @Getter @Setter @NoArgsConstructor
public class PrivacyRequestJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="request_code") private String requestCode;
    @Column(name="request_type", nullable=false) private String requestType;
    @Column(name="submitted_channel", nullable=false) private String submittedChannel = "INTERNAL";
    @Column(nullable=false) private String status;
    @Column(name="subject_reference") private String subjectReference;
    @Column(name="assigned_owner_user_id") private UUID assignedOwnerUserId;
    @Column(name="resolution_summary") private String resolutionSummary;
    @Column(name="rejection_reason") private String rejectionReason;
    @Column(name="completed_at") private Instant completedAt;
    @Version private Integer version;
}
