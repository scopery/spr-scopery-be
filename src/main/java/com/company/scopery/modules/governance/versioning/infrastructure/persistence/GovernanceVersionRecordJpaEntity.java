package com.company.scopery.modules.governance.versioning.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.governance.shared.constant.GovernanceTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = GovernanceTableNames.VERSION_RECORD) @Getter @Setter @NoArgsConstructor
public class GovernanceVersionRecordJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="domain_version_type") private String domainVersionType;
    @Column(name="domain_version_id") private UUID domainVersionId;
    @Column(name="snapshot_id") private UUID snapshotId;
    @Column(name="change_type") private String changeType;
    @Column(name="change_reason") private String changeReason;
    @Column(name="current_flag", nullable=false) private boolean currentFlag;
    @Column(name="finalized_flag", nullable=false) private boolean finalizedFlag;
    @Column(name="restore_eligible", nullable=false) private boolean restoreEligible;
    @Column(name="version_number", nullable=false) private int versionNumber;
    @Version private Integer version;
}
