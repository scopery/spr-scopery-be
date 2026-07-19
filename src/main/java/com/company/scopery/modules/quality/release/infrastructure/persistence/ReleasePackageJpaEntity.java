package com.company.scopery.modules.quality.release.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity; import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*; import java.time.*; import java.util.UUID;
@Entity @Table(name=QualityTableNames.RELEASE_PACKAGE) @Getter @Setter @NoArgsConstructor
public class ReleasePackageJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(nullable=false) private String code; @Column(name="version_label", nullable=false) private String versionLabel;
    @Column(nullable=false) private String name; @Column(columnDefinition="text") private String description;
    @Column(name="release_type", nullable=false) private String releaseType; @Column(nullable=false) private String status;
    @Column(name="planned_release_date") private LocalDate plannedReleaseDate;
    @Column(name="actual_release_date") private LocalDate actualReleaseDate;
    @Column(name="readiness_status") private String readinessStatus;
    @Column(name="readiness_summary_json", columnDefinition="text") private String readinessSummaryJson;
    @Column(name="release_notes", columnDefinition="text") private String releaseNotes;
    @Column(name="approved_at") private Instant approvedAt; @Column(name="approved_by") private UUID approvedBy;
    @Column(name="released_at") private Instant releasedAt; @Column(name="released_by") private UUID releasedBy;
    @Column(name="archived_at") private Instant archivedAt; @Column(name="archived_by") private UUID archivedBy;
    @Column(name="trace_id") private String traceId; @Version private Integer version;
}
