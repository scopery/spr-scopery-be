package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.SYNC_JOB) @Getter @Setter @NoArgsConstructor
public class SyncJobJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="connection_id", nullable=false) private UUID connectionId;
    @Column(name="mapping_profile_id") private UUID mappingProfileId;
    @Column(name="sync_code") private String syncCode;
    @Column(nullable=false) private String name;
    @Column(name="sync_direction", nullable=false) private String syncDirection;
    @Column(name="sync_mode", nullable=false) private String syncMode;
    @Column(name="object_scope", nullable=false) private String objectScope;
    @Column(name="conflict_strategy", nullable=false) private String conflictStrategy;
    @Column(name="schedule_json", columnDefinition="jsonb") private String scheduleJson;
    @Column(nullable=false) private String status;
    @Column(name="disabled_at") private Instant disabledAt;
    @Column(name="archived_at") private Instant archivedAt;
    @Version private Integer version;
}
