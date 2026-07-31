package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name=QualityTableNames.RUN_MEMBERSHIP) @Getter @Setter @NoArgsConstructor
public class TestRunMembershipJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="test_run_id", nullable=false) private UUID testRunId;
    @Column(name="case_kind", nullable=false) private String caseKind;
    @Column(name="case_id", nullable=false) private UUID caseId;
    @Column(name="display_order", nullable=false) private int displayOrder;
    @Column(name="created_at", nullable=false) private Instant createdAt;
}
