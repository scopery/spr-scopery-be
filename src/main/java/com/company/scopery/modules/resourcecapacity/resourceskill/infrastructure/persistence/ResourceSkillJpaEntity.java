package com.company.scopery.modules.resourcecapacity.resourceskill.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = CapacityTableNames.RESOURCE_SKILL) @Getter @Setter @NoArgsConstructor
public class ResourceSkillJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="skill_code", nullable=false) private String skillCode;
    @Column(nullable=false) private String name;
    private String description;
    @Column(name="default_rate_card_id") private UUID defaultRateCardId;
    @Column(nullable=false) private String status;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
