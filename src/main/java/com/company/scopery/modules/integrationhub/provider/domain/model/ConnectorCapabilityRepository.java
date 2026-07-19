package com.company.scopery.modules.integrationhub.provider.domain.model;
import java.util.List;
public interface ConnectorCapabilityRepository {
    ConnectorCapability save(ConnectorCapability c);
    List<ConnectorCapability> findByProviderCode(String providerCode);
    List<ConnectorCapability> findAll();
}
