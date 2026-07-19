package com.company.scopery.modules.resourcecapacity.actualeffort.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name = CapacityTableNames.RESOURCE_ACTUAL_EFFORT) @Getter @Setter @NoArgsConstructor
public class ActualEffortRecordJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="resource_profile_id") private UUID resourceProfileId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(name="effort_date", nullable=false) private LocalDate effortDate;
    @Column(name="effort_hours", nullable=false) private BigDecimal effortHours;
    @Column(name="input_mode", nullable=false) private String inputMode;
    private String description;
    @Column(nullable=false) private String status;
    @Column(name="cancelled_at") private Instant cancelledAt;
    @Column(name="cancelled_by") private UUID cancelledBy;
    @Column(name="cancellation_reason") private String cancellationReason;
    @Version private Integer version;
}
