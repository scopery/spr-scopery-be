package com.company.scopery.modules.trust.anonymization.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = TrustTableNames.ANONYMIZATION_PLAN) @Getter @Setter @NoArgsConstructor
public class AnonymizationPlanJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "data_subject_index_id") private UUID dataSubjectIndexId;
    @Column(nullable = false, length = 50) private String status;
    @Column(name = "plan_json", columnDefinition = "TEXT") private String planJson;
    @Column(name = "dry_run_result_json", columnDefinition = "TEXT") private String dryRunResultJson;
    @Column(name = "legal_hold_blocked", nullable = false) private boolean legalHoldBlocked;
    @Column(columnDefinition = "TEXT") private String reason;
    @Column(name = "executed_at") private Instant executedAt;
    @Column(name = "cancelled_at") private Instant cancelledAt;
    @Version private Integer version;
}
