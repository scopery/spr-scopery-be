package com.company.scopery.modules.integrationhub.provider.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.PROVIDER) @Getter @Setter @NoArgsConstructor
public class IntegrationProviderJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="provider_code", nullable=false, unique=true) private String providerCode;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String category;
    @Column(columnDefinition="text") private String description;
    @Column(name="adapter_key") private String adapterKey;
    @Column(nullable=false) private Boolean enabled = true;
    @Column(name="seed_only", nullable=false) private Boolean seedOnly = false;
    @Column(name="capabilities_json", columnDefinition="jsonb") private String capabilitiesJson;
    @Version private Integer version;
}
