package com.company.scopery.modules.governance.grant.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.governance.shared.constant.GovernanceTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = GovernanceTableNames.ACCESS_GRANT) @Getter @Setter @NoArgsConstructor
public class ObjectAccessGrantJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="grantee_type", nullable=false) private String granteeType;
    @Column(name="grantee_id", nullable=false) private UUID granteeId;
    @Column(name="grant_role", nullable=false) private String grantRole;
    @Column(nullable=false) private String status;
    @Column(name="expires_at") private Instant expiresAt;
    @Version private Integer version;
}
