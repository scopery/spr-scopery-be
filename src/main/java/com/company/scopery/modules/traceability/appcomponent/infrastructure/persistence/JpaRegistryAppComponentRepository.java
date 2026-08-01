package com.company.scopery.modules.traceability.appcomponent.infrastructure.persistence;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.appcomponent.infrastructure.mapper.RegistryAppComponentPersistenceMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    @Override public Optional<RegistryAppComponent> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }
    @Override public List<RegistryAppComponent> findByIdIn(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return springData.findAllById(ids).stream().map(mapper::toDomain).toList();
    }
    @Override public Optional<RegistryAppComponent> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<RegistryAppComponent> findByApplicationId(UUID applicationId) {
        return springData.findByApplicationIdOrderByCreatedAtDesc(applicationId).stream().map(mapper::toDomain).toList();
    }
    @Override public void delete(UUID id, UUID workspaceId) { springData.deleteByIdAndWorkspaceId(id, workspaceId); }

    @Override
    public PageResult<RegistryAppComponent> searchByScreenId(UUID screenId, String query, int page, int size) {
        String q = (query == null || query.isBlank()) ? null : query.trim();
        var pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return PageResult.fromSpringPage(
                springData.searchByScreenId(screenId, q, pageable).map(mapper::toDomain));
    }
}
