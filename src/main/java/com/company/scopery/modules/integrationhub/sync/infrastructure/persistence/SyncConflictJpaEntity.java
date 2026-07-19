package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.SYNC_CONFLICT) @Getter @Setter @NoArgsConstructor
public class SyncConflictJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="sync_job_id", nullable=false) private UUID syncJobId;
    @Column(name="sync_run_id") private UUID syncRunId;
    @Column(name="connection_id", nullable=false) private UUID connectionId;
    @Column(name="conflict_type", nullable=false) private String conflictType;
    @Column(name="scopery_object_type") private String scoperyObjectType;
    @Column(name="scopery_object_id") private UUID scoperyObjectId;
    @Column(name="external_object_type") private String externalObjectType;
    @Column(name="external_id") private String externalId;
    @Column(nullable=false) private String severity;
    @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String description;
    @Column(name="resolution_strategy") private String resolutionStrategy;
    @Column(name="resolved_at") private Instant resolvedAt;
    @Column(name="resolved_by") private UUID resolvedBy;
    @Column(name="resolution_notes", columnDefinition="text") private String resolutionNotes;
    @Version private Integer version;
}
