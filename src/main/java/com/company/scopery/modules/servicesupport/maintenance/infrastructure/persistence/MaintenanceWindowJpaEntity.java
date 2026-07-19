package com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = SupportTableNames.MAINTENANCE_WINDOW) @Getter @Setter @NoArgsConstructor
public class MaintenanceWindowJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "maintenance_plan_id", nullable = false) private UUID maintenancePlanId;
    @Column private String title;
    @Column(name = "window_start", nullable = false) private Instant windowStart;
    @Column(name = "window_end", nullable = false) private Instant windowEnd;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}
