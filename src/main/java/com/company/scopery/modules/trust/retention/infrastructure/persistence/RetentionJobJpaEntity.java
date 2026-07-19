package com.company.scopery.modules.trust.retention.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.RETENTION_JOB) @Getter @Setter @NoArgsConstructor
public class RetentionJobJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="retention_policy_id") private UUID retentionPolicyId;
    @Column(name="job_mode", nullable=false) private String jobMode;
    @Column(nullable=false) private String status;
    @Column(name="candidate_count", nullable=false) private Long candidateCount = 0L;
    @Column(name="actioned_count", nullable=false) private Long actionedCount = 0L;
    @Column(name="skipped_legal_hold_count", nullable=false) private Long skippedLegalHoldCount = 0L;
    @Column(name="failed_count", nullable=false) private Long failedCount = 0L;
    @Column(name="completed_at") private Instant completedAt;
    @Version private Integer version;
}
