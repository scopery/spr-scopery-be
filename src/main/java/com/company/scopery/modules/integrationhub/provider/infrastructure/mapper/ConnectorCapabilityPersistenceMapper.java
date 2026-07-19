package com.company.scopery.modules.integrationhub.provider.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.provider.domain.model.ConnectorCapability;
import com.company.scopery.modules.integrationhub.provider.infrastructure.persistence.ConnectorCapabilityJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ConnectorCapabilityPersistenceMapper {
    public ConnectorCapabilityJpaEntity toJpaEntity(ConnectorCapability d) {
        ConnectorCapabilityJpaEntity e = new ConnectorCapabilityJpaEntity();
        e.setId(d.id()); e.setProviderCode(d.providerCode()); e.setCapabilityCode(d.capabilityCode());
        e.setDirection(d.direction()); e.setEnabled(d.enabled()); e.setDescription(d.description());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ConnectorCapability toDomain(ConnectorCapabilityJpaEntity e) {
        return new ConnectorCapability(e.getId(), e.getProviderCode(), e.getCapabilityCode(), e.getDirection(),
                e.getEnabled() != null && e.getEnabled(), e.getDescription(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
