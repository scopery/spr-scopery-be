package com.company.scopery.modules.quality.teststep.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = QualityTableNames.TEST_STEP) @Getter @Setter @NoArgsConstructor
public class TestStepJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="test_case_id", nullable=false) private UUID testCaseId;
    @Column(name="step_order", nullable=false) private Integer stepOrder;
    @Column(name="action_text", nullable=false, columnDefinition="text") private String actionText;
    @Column(name="expected_result", nullable=false, columnDefinition="text") private String expectedResult;
    @Column(name="data_notes", columnDefinition="text") private String dataNotes;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
