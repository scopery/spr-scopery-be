package com.company.scopery.modules.quality.teststep.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = QualityTableNames.TEST_CASE_STEP) @Getter @Setter @NoArgsConstructor
public class TestCaseStepJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="test_case_id", nullable=false) private UUID testCaseId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="sort_order", nullable=false) private int sortOrder;
    @Column(name="action", nullable=false, columnDefinition="text") private String action;
    @Column(name="expected_result", columnDefinition="text") private String expectedResult;
    @Column(name="screen_id") private UUID screenId;
    @Column(name="component_id") private UUID componentId;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
