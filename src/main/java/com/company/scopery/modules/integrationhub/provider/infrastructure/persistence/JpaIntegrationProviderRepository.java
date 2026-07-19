package com.company.scopery.modules.integrationhub.provider.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.provider.domain.model.IntegrationProvider;
import com.company.scopery.modules.integrationhub.provider.domain.model.IntegrationProviderRepository;
import com.company.scopery.modules.integrationhub.provider.infrastructure.mapper.IntegrationProviderPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional;
@Repository
public class JpaIntegrationProviderRepository implements IntegrationProviderRepository {
    private final SpringDataIntegrationProviderJpaRepository spring;
    private final IntegrationProviderPersistenceMapper mapper;
    public JpaIntegrationProviderRepository(SpringDataIntegrationProviderJpaRepository spring, IntegrationProviderPersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public IntegrationProvider save(IntegrationProvider p){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(p))); }
    @Override public Optional<IntegrationProvider> findByCode(String code){ return spring.findByProviderCode(code).map(mapper::toDomain); }
    @Override public List<IntegrationProvider> findAll(){ return spring.findAll().stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByCode(String code){ return spring.existsByProviderCode(code); }
}
