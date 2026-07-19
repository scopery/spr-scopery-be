package com.company.scopery.modules.governance.policy.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.governance.shared.constant.GovernanceTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = GovernanceTableNames.POLICY) @Getter @Setter @NoArgsConstructor
public class GovernancePolicyJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="versioning_mode") private String versioningMode;
    @Column(name="version_on_update", nullable=false) private boolean versionOnUpdate;
    @Column(name="lock_on_finalize", nullable=false) private boolean lockOnFinalize;
    @Column(name="allow_restore", nullable=false) private boolean allowRestore;
    @Column(name="allow_owner_grant", nullable=false) private boolean allowOwnerGrant;
    @Column(name="baseline_guard_mode") private String baselineGuardMode;
    @Column(name="audit_level") private String auditLevel;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
