package com.company.scopery.modules.governance.versioning.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.governance.shared.constant.GovernanceTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = GovernanceTableNames.RESTORE) @Getter @Setter @NoArgsConstructor
public class RestoreRequestJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="restore_from_version_record_id", nullable=false) private UUID restoreFromVersionRecordId;
    @Column(nullable=false) private String status;
    private String reason;
    @Column(name="completed_at") private Instant completedAt;
    @Version private Integer version;
}
