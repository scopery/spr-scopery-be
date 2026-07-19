package com.company.scopery.modules.integrationhub.provider.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.provider.domain.model.IntegrationProvider;
import com.company.scopery.modules.integrationhub.provider.infrastructure.persistence.IntegrationProviderJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class IntegrationProviderPersistenceMapper {
    public IntegrationProviderJpaEntity toJpaEntity(IntegrationProvider d) {
        IntegrationProviderJpaEntity e = new IntegrationProviderJpaEntity();
        e.setId(d.id()); e.setProviderCode(d.providerCode()); e.setName(d.name()); e.setCategory(d.category());
        e.setDescription(d.description()); e.setAdapterKey(d.adapterKey());
        e.setEnabled(d.enabled()); e.setSeedOnly(d.seedOnly()); e.setCapabilitiesJson(d.capabilitiesJson());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public IntegrationProvider toDomain(IntegrationProviderJpaEntity e) {
        return new IntegrationProvider(e.getId(), e.getProviderCode(), e.getName(), e.getCategory(), e.getDescription(),
                e.getAdapterKey(),
                e.getEnabled() != null && e.getEnabled(),
                e.getSeedOnly() != null && e.getSeedOnly(),
                e.getCapabilitiesJson(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
}
