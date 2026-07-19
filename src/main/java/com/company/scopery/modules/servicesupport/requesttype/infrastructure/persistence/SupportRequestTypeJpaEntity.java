package com.company.scopery.modules.servicesupport.requesttype.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = SupportTableNames.REQUEST_TYPE) @Getter @Setter @NoArgsConstructor
public class SupportRequestTypeJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "type_code", nullable = false) private String typeCode;
    @Column(nullable = false) private String name;
    @Column private String description;
    @Column(name = "default_priority") private String defaultPriority;
    @Column(name = "portal_visible", nullable = false) private boolean portalVisible;
    @Column(nullable = false) private boolean enabled;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}
