package com.company.scopery.modules.quality.qualityplan.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name = QualityTableNames.QUALITY_PLAN) @Getter @Setter @NoArgsConstructor
public class QualityPlanJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="source_baseline_id") private UUID sourceBaselineId;
    private String code;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private String status;
    @Column(name="current_flag", nullable=false) private boolean currentFlag;
    @Column(name="quality_objectives", columnDefinition="text") private String qualityObjectives;
    @Column(name="test_strategy", columnDefinition="text") private String testStrategy;
    @Column(name="entry_criteria", columnDefinition="text") private String entryCriteria;
    @Column(name="exit_criteria", columnDefinition="text") private String exitCriteria;
    @Column(name="defect_policy_json", columnDefinition="text") private String defectPolicyJson;
    @Column(name="release_readiness_policy_json", columnDefinition="text") private String releaseReadinessPolicyJson;
    @Column(name="approved_at") private Instant approvedAt;
    @Column(name="approved_by") private UUID approvedBy;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Column(name="trace_id") private String traceId;
    @Version private Integer version;
}
