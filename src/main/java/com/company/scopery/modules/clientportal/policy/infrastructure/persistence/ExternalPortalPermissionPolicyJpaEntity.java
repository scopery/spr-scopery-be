package com.company.scopery.modules.clientportal.policy.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalTableNames;
import jakarta.persistence.*; import lombok.*; import java.util.UUID;
@Entity @Table(name = ClientPortalTableNames.PERMISSION_POLICY) @Getter @Setter @NoArgsConstructor
public class ExternalPortalPermissionPolicyJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(nullable = false) private String code;
    @Column(nullable = false) private String name;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "permissions_json", columnDefinition = "TEXT") private String permissionsJson;
    @Version private Integer version;
}
