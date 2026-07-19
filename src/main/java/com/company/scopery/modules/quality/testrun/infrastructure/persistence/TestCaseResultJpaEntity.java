package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity; import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name=QualityTableNames.TEST_CASE_RESULT) @Getter @Setter @NoArgsConstructor
public class TestCaseResultJpaEntity extends AuditableJpaEntity {
    @Id private UUID id; @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="test_run_id", nullable=false) private UUID testRunId; @Column(name="test_case_id", nullable=false) private UUID testCaseId;
    @Column(name="result_status", nullable=false) private String resultStatus;
    @Column(name="actual_result", columnDefinition="text") private String actualResult;
    @Column(name="evidence_reference", columnDefinition="text") private String evidenceReference;
    @Column(name="executed_at") private Instant executedAt; @Column(name="executed_by") private UUID executedBy;
    @Column(name="defect_id") private UUID defectId; @Version private Integer version;
}
