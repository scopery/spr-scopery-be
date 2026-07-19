package com.company.scopery.modules.quality.deployment.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = QualityTableNames.DEPLOYMENT_RECORD) @Getter @Setter @NoArgsConstructor
public class DeploymentRecordJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="release_package_id", nullable=false) private UUID releasePackageId;
    @Column(name="deployment_environment_id", nullable=false) private UUID deploymentEnvironmentId;
    @Column(nullable=false) private String status;
    @Column(name="build_reference") private String buildReference;
    @Column(name="deployment_reference") private String deploymentReference;
    @Column(name="started_at") private Instant startedAt;
    @Column(name="completed_at") private Instant completedAt;
    @Column(name="deployed_by") private UUID deployedBy;
    @Column(name="failure_reason", columnDefinition="text") private String failureReason;
    @Column(name="rollback_plan_id") private UUID rollbackPlanId;
    @Column(name="rolled_back_at") private Instant rolledBackAt;
    @Column(name="rolled_back_by") private UUID rolledBackBy;
    @Column(name="rollback_reason", columnDefinition="text") private String rollbackReason;
    @Column(name="trace_id") private String traceId;
    @Version private Integer version;
}
