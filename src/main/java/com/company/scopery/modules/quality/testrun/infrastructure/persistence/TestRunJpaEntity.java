package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity; import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name=QualityTableNames.TEST_RUN) @Getter @Setter @NoArgsConstructor
public class TestRunJpaEntity extends AuditableJpaEntity {
    @Id private UUID id; @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="test_plan_id") private UUID testPlanId; @Column(name="test_suite_id") private UUID testSuiteId;
    @Column(name="release_package_id") private UUID releasePackageId;
    @Column(name="deployment_environment_id") private UUID deploymentEnvironmentId;
    @Column(nullable=false) private String name; @Column(name="run_type", nullable=false) private String runType;
    @Column(nullable=false) private String status; @Column(name="started_at") private Instant startedAt;
    @Column(name="completed_at") private Instant completedAt; @Column(name="executed_by") private UUID executedBy;
    @Column(name="summary_json", columnDefinition="text") private String summaryJson;
    @Column(name="archived_at") private Instant archivedAt; @Column(name="archived_by") private UUID archivedBy;
    @Column(name="trace_id") private String traceId; @Version private Integer version;
}
