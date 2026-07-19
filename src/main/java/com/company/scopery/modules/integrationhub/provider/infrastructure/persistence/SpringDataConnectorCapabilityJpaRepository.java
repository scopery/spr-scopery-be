package com.company.scopery.modules.integrationhub.provider.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataConnectorCapabilityJpaRepository extends JpaRepository<ConnectorCapabilityJpaEntity, UUID> {
    List<ConnectorCapabilityJpaEntity> findByProviderCode(String providerCode);
}
