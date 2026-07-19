package com.company.scopery.modules.quality.releaseitem.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = QualityTableNames.RELEASE_ITEM) @Getter @Setter @NoArgsConstructor
public class ReleaseItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="release_package_id", nullable=false) private UUID releasePackageId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(nullable=false) private Boolean required;
    @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String notes;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
