package com.company.scopery.modules.servicesupport.costinput.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.util.UUID;
@Entity @Table(name = SupportTableNames.COST_INPUT) @Getter @Setter @NoArgsConstructor
public class ServiceCostInputJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "service_profile_id") private UUID serviceProfileId;
    @Column(name = "support_case_id") private UUID supportCaseId;
    @Column(name = "incident_id") private UUID incidentId;
    @Column(name = "maintenance_activity_id") private UUID maintenanceActivityId;
    @Column(name = "resource_profile_id") private UUID resourceProfileId;
    @Column(name = "source_type", nullable = false) private String sourceType;
    @Column(name = "source_id") private UUID sourceId;
    @Column(name = "effort_hours") private BigDecimal effortHours;
    @Column(name = "rate_amount") private BigDecimal rateAmount;
    @Column private String currency;
    @Column(name = "cost_amount", nullable = false) private BigDecimal costAmount;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}
