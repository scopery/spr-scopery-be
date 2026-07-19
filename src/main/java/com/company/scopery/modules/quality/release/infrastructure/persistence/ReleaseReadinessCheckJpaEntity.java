package com.company.scopery.modules.quality.release.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity; import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name=QualityTableNames.RELEASE_READINESS_CHECK) @Getter @Setter @NoArgsConstructor
public class ReleaseReadinessCheckJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="release_package_id", nullable=false) private UUID releasePackageId;
    @Column(name="check_code", nullable=false) private String checkCode;
    @Column(name="check_name", nullable=false) private String checkName;
    @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String details;
    @Column(nullable=false) private boolean blocking;
    @Column(name="override_reason", columnDefinition="text") private String overrideReason;
    @Column(name="overridden_at") private Instant overriddenAt; @Column(name="overridden_by") private UUID overriddenBy;
    @Version private Integer version;
}
