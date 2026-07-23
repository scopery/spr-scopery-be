package com.company.scopery.modules.traceability.appcomponent.infrastructure.persistence;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.appcomponent.infrastructure.mapper.RegistryAppComponentPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRegistryAppComponentRepository implements RegistryAppComponentRepository {
    private final SpringDataRegistryAppComponentJpaRepository springData;
    private final RegistryAppComponentPersistenceMapper mapper;
    public JpaRegistryAppComponentRepository(SpringDataRegistryAppComponentJpaRepository springData, RegistryAppComponentPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RegistryAppComponent save(RegistryAppComponent e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RegistryAppComponent> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<RegistryAppComponent> findByApplicationId(UUID applicationId) {
        return springData.findByApplicationIdOrderByCreatedAtDesc(applicationId).stream().map(mapper::toDomain).toList();
    }
    @Override public void delete(UUID id, UUID workspaceId) { springData.deleteByIdAndWorkspaceId(id, workspaceId); }
}
