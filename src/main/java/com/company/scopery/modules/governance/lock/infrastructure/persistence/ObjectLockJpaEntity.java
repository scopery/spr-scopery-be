package com.company.scopery.modules.governance.lock.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.governance.shared.constant.GovernanceTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = GovernanceTableNames.LOCK) @Getter @Setter @NoArgsConstructor
public class ObjectLockJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="lock_type", nullable=false) private String lockType;
    @Column(nullable=false) private String status;
    @Column(name="locked_at") private Instant lockedAt;
    @Column(name="locked_by") private UUID lockedBy;
    @Column(name="released_at") private Instant releasedAt;
    @Column(name="released_by") private UUID releasedBy;
    @Column private String reason;
    @Version private Integer version;
}
