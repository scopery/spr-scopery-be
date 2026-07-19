package com.company.scopery.modules.quality.coverage.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = QualityTableNames.TEST_CASE_COVERAGE) @Getter @Setter @NoArgsConstructor
public class TestCaseCoverageJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="test_case_id", nullable=false) private UUID testCaseId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="coverage_type", nullable=false) private String coverageType;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
