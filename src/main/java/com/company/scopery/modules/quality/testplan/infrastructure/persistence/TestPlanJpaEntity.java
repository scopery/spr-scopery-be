package com.company.scopery.modules.quality.testplan.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name = QualityTableNames.TEST_PLAN) @Getter @Setter @NoArgsConstructor
public class TestPlanJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="quality_plan_id") private UUID qualityPlanId;
    @Column(name="release_package_id") private UUID releasePackageId;
    private String code;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
    @Column(name="test_level", nullable=false) private String testLevel;
    @Column(nullable=false) private String status;
    @Column(name="approved_at") private Instant approvedAt;
    @Column(name="approved_by") private UUID approvedBy;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
