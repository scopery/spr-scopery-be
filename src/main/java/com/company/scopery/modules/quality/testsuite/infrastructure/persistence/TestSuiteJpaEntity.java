package com.company.scopery.modules.quality.testsuite.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = QualityTableNames.TEST_SUITE) @Getter @Setter @NoArgsConstructor
public class TestSuiteJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="test_plan_id", nullable=false) private UUID testPlanId;
    @Column(name="deliverable_id") private UUID deliverableId;
    @Column(name="scope_item_id") private UUID scopeItemId;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private String status;
    @Column(name="sort_order") private Integer sortOrder;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
