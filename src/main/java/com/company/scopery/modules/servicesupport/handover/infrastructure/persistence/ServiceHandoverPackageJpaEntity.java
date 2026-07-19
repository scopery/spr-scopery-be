package com.company.scopery.modules.servicesupport.handover.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = SupportTableNames.HANDOVER_PACKAGE) @Getter @Setter @NoArgsConstructor
public class ServiceHandoverPackageJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "service_profile_id") private UUID serviceProfileId;
    @Column(name = "package_code") private String packageCode;
    @Column(nullable = false) private String title;
    @Column private String summary;
    @Column(nullable = false) private String status;
    @Column(name = "client_visible", nullable = false) private boolean clientVisible;
    @Column(name = "finalized_at") private Instant finalizedAt;
    @Column(name = "finalized_by") private UUID finalizedBy;
    @Version private Integer version;
}
