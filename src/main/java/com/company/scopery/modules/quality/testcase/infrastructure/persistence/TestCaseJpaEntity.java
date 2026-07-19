package com.company.scopery.modules.quality.testcase.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name=QualityTableNames.TEST_CASE) @Getter @Setter @NoArgsConstructor
public class TestCaseJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="test_suite_id") private UUID testSuiteId;
    private String code; @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private String type; @Column(nullable=false) private String priority; @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String preconditions;
    @Column(name="expected_result", columnDefinition="text") private String expectedResult;
    @Column(name="version_number", nullable=false) private int versionNumber;
    @Column(name="approved_at") private Instant approvedAt; @Column(name="approved_by") private UUID approvedBy;
    @Column(name="archived_at") private Instant archivedAt; @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
