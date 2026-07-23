package com.company.scopery.modules.traceability.apiendpoint.infrastructure.persistence;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.*;
import com.company.scopery.modules.traceability.apiendpoint.infrastructure.mapper.RegistryApiEndpointPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRegistryApiEndpointRepository implements RegistryApiEndpointRepository {
    private final SpringDataRegistryApiEndpointJpaRepository springData;
    private final RegistryApiEndpointPersistenceMapper mapper;
    public JpaRegistryApiEndpointRepository(SpringDataRegistryApiEndpointJpaRepository springData, RegistryApiEndpointPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RegistryApiEndpoint save(RegistryApiEndpoint e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RegistryApiEndpoint> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public Optional<RegistryApiEndpoint> findByIdAndApplicationId(UUID id, UUID applicationId) {
        return springData.findByIdAndApplicationId(id, applicationId).map(mapper::toDomain);
    }
    @Override public List<RegistryApiEndpoint> findByApplicationId(UUID applicationId) {
        return springData.findByApplicationIdOrderByCreatedAtDesc(applicationId).stream().map(mapper::toDomain).toList();
    }
    @Override public void delete(UUID id, UUID applicationId) { springData.deleteByIdAndApplicationId(id, applicationId); }
}
