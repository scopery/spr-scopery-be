package com.company.scopery.modules.quality.verificationresult.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity @Table(name=QualityTableNames.VERIFICATION_CASE_RESULT) @Getter @Setter @NoArgsConstructor
public class VerificationCaseResultJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="test_run_id", nullable=false) private UUID testRunId;
    @Column(name="verification_case_id", nullable=false) private UUID verificationCaseId;
    @Column(name="result_status", nullable=false) private String resultStatus;
    @Column(name="actual_value", precision=20, scale=4) private BigDecimal actualValue;
    @Column(name="actual_value_unit") private String actualValueUnit;
    @Column(name="actual_result_json", columnDefinition="text") private String actualResultJson;
    @Column(name="evidence_reference", columnDefinition="text") private String evidenceReference;
    @Column(name="executed_at") private Instant executedAt;
    @Column(name="executed_by_id") private UUID executedById;
    @Column(name="defect_id") private UUID defectId;
    @Column(name="comment", columnDefinition="text") private String comment;
    @Version private Integer version;
}
