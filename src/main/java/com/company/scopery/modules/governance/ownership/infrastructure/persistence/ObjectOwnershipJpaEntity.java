package com.company.scopery.modules.governance.ownership.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.governance.shared.constant.GovernanceTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = GovernanceTableNames.OWNERSHIP) @Getter @Setter @NoArgsConstructor
public class ObjectOwnershipJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="owner_target_type", nullable=false) private String ownerTargetType;
    @Column(name="owner_target_id", nullable=false) private UUID ownerTargetId;
    @Column(nullable=false) private String status;
    @Column(name="assigned_at") private Instant assignedAt; @Column(name="assigned_by") private UUID assignedBy;
    @Column(name="revoked_at") private Instant revokedAt; @Column(name="revoked_by") private UUID revokedBy;
    @Version private Integer version;
}
