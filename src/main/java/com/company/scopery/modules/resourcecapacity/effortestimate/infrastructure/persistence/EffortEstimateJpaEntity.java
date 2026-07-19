package com.company.scopery.modules.resourcecapacity.effortestimate.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CapacityTableNames.RESOURCE_EFFORT_ESTIMATE) @Getter @Setter @NoArgsConstructor
public class EffortEstimateJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="resource_role_id") private UUID resourceRoleId;
    @Column(name="resource_profile_id") private UUID resourceProfileId;
    @Column(name="estimate_type", nullable=false) private String estimateType;
    @Column(name="effort_hours", nullable=false) private BigDecimal effortHours;
    @Column(name="confidence_percent") private BigDecimal confidencePercent;
    private String reason;
    @Column(nullable=false) private String status;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
