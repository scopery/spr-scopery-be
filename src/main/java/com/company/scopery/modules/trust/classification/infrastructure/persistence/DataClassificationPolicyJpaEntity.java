package com.company.scopery.modules.trust.classification.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.trust.shared.constant.TrustTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = TrustTableNames.DATA_CLASSIFICATION_POLICY) @Getter @Setter @NoArgsConstructor
public class DataClassificationPolicyJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false, unique = true) private UUID workspaceId;
    @Column(name = "policy_code", length = 100, nullable = false) private String policyCode;
    @Column(nullable = false) private String name;
    @Column(name = "default_classification", length = 50, nullable = false) private String defaultClassification;
    @Column(nullable = false) private boolean enabled;
    @Version private Integer version;
}
