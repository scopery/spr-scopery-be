package com.company.scopery.modules.trust.retention.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = TrustTableNames.RETENTION_POLICY) @Getter @Setter @NoArgsConstructor
public class RetentionPolicyJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="policy_code", nullable=false) private String policyCode;
    @Column(nullable=false) private String name;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    private String classification;
    @Column(name="retention_period_days", nullable=false) private Integer retentionPeriodDays;
    @Column(name="retention_action", nullable=false) private String retentionAction;
    @Column(name="review_required", nullable=false) private Boolean reviewRequired = true;
    @Column(nullable=false) private Boolean enabled = true;
    @Version private Integer version;
}
