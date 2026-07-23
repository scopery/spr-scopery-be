package com.company.scopery.modules.traceability.dataentity.infrastructure.persistence;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntityRepository;
import com.company.scopery.modules.traceability.dataentity.infrastructure.mapper.RegistryDataEntityPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRegistryDataEntityRepository implements RegistryDataEntityRepository {
    private final SpringDataRegistryDataEntityJpaRepository springData;
    private final RegistryDataEntityPersistenceMapper mapper;
    public JpaRegistryDataEntityRepository(SpringDataRegistryDataEntityJpaRepository springData, RegistryDataEntityPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public RegistryDataEntity save(RegistryDataEntity e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<RegistryDataEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<RegistryDataEntity> findByApplicationId(UUID applicationId) {
        return springData.findByApplicationIdOrderByCreatedAtDesc(applicationId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<RegistryDataEntity> findByApplicationIdAndModuleId(UUID applicationId, UUID moduleId) {
        return springData.findByApplicationIdAndModuleIdOrderByCreatedAtDesc(applicationId, moduleId).stream().map(mapper::toDomain).toList();
    }
    @Override public void delete(UUID id, UUID workspaceId) { springData.deleteByIdAndWorkspaceId(id, workspaceId); }
}
