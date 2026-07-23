package com.company.scopery.modules.traceability.appmodule.infrastructure.persistence;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModule;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModuleRepository;
import com.company.scopery.modules.traceability.appmodule.infrastructure.mapper.RegistryAppModulePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRegistryAppModuleRepository implements RegistryAppModuleRepository {
    private final SpringDataRegistryAppModuleJpaRepository springData;
    private final RegistryAppModulePersistenceMapper mapper;
    public JpaRegistryAppModuleRepository(SpringDataRegistryAppModuleJpaRepository springData, RegistryAppModulePersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public RegistryAppModule save(RegistryAppModule e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RegistryAppModule> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<RegistryAppModule> findByApplicationId(UUID applicationId) {
        return springData.findByApplicationIdOrderByCreatedAtDesc(applicationId).stream().map(mapper::toDomain).toList();
    }
    @Override public void delete(UUID id, UUID workspaceId) { springData.deleteByIdAndWorkspaceId(id, workspaceId); }
}
