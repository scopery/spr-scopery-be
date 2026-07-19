package com.company.scopery.modules.integrationhub.provider.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.CAPABILITY) @Getter @Setter @NoArgsConstructor
public class ConnectorCapabilityJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="provider_code", nullable=false) private String providerCode;
    @Column(name="capability_code", nullable=false) private String capabilityCode;
    @Column(nullable=false) private String direction;
    @Column(nullable=false) private Boolean enabled = true;
    @Column(columnDefinition="text") private String description;
    @Version private Integer version;
}
