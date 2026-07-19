package com.company.scopery.modules.quality.rollbackplan.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = QualityTableNames.ROLLBACK_PLAN) @Getter @Setter @NoArgsConstructor
public class RollbackPlanJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="release_package_id") private UUID releasePackageId;
    @Column(name="deployment_record_id") private UUID deploymentRecordId;
    @Column(nullable=false) private String title;
    @Column(nullable=false, columnDefinition="text") private String description;
    @Column(name="owner_user_id") private UUID ownerUserId;
    @Column(nullable=false) private String status;
    @Column(name="steps_json", columnDefinition="text") private String stepsJson;
    @Column(name="approved_at") private Instant approvedAt;
    @Column(name="approved_by") private UUID approvedBy;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
