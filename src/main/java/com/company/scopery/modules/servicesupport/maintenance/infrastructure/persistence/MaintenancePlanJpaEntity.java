package com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = SupportTableNames.MAINTENANCE_PLAN) @Getter @Setter @NoArgsConstructor
public class MaintenancePlanJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String status;
    @Column(name="planned_start") private Instant plannedStart;
    @Column(name="planned_end") private Instant plannedEnd;
    @Version private Integer version;
}
