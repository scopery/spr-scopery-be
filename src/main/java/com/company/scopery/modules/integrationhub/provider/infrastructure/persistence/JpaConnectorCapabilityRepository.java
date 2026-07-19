package com.company.scopery.modules.integrationhub.provider.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.provider.domain.model.ConnectorCapability;
import com.company.scopery.modules.integrationhub.provider.domain.model.ConnectorCapabilityRepository;
import com.company.scopery.modules.integrationhub.provider.infrastructure.mapper.ConnectorCapabilityPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class JpaConnectorCapabilityRepository implements ConnectorCapabilityRepository {
    private final SpringDataConnectorCapabilityJpaRepository spring;
    private final ConnectorCapabilityPersistenceMapper mapper;
    public JpaConnectorCapabilityRepository(SpringDataConnectorCapabilityJpaRepository spring, ConnectorCapabilityPersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ConnectorCapability save(ConnectorCapability c){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(c))); }
    @Override public List<ConnectorCapability> findByProviderCode(String providerCode){ return spring.findByProviderCode(providerCode).stream().map(mapper::toDomain).toList(); }
    @Override public List<ConnectorCapability> findAll(){ return spring.findAll().stream().map(mapper::toDomain).toList(); }
}
