package com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity @Table(name = SupportTableNames.MAINTENANCE_ACTIVITY) @Getter @Setter @NoArgsConstructor
public class MaintenanceActivityJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "maintenance_window_id") private UUID maintenanceWindowId;
    @Column(name = "maintenance_plan_id") private UUID maintenancePlanId;
    @Column(name = "service_profile_id") private UUID serviceProfileId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "activity_type", nullable = false) private String activityType;
    @Column(nullable = false) private String title;
    @Column private String description;
    @Column(name = "outcome_summary") private String outcomeSummary;
    @Column(name = "effort_hours") private BigDecimal effortHours;
    @Column(name = "client_visible", nullable = false) private boolean clientVisible;
    @Column(name = "performed_at") private Instant performedAt;
    @Column(name = "performed_by") private UUID performedBy;
    @Version private Integer version;
}
