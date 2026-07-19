package com.company.scopery.modules.scope.scopeitem.infrastructure.persistence;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItem;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopeitem.infrastructure.mapper.ScopeItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaScopeItemRepository implements ScopeItemRepository {
    private final SpringDataScopeItemJpaRepository springData; private final ScopeItemPersistenceMapper mapper;
    public JpaScopeItemRepository(SpringDataScopeItemJpaRepository springData, ScopeItemPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ScopeItem save(ScopeItem item){ return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item))); }
    @Override public Optional<ScopeItem> findByIdAndProjectId(UUID id, UUID projectId){ return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<ScopeItem> findByScopePackageId(UUID scopePackageId){ return springData.findByScopePackageIdOrderBySortOrderAscCreatedAtAsc(scopePackageId).stream().map(mapper::toDomain).toList(); }
    @Override public List<ScopeItem> findByProjectId(UUID projectId){ return springData.findByProjectIdOrderByCreatedAtAsc(projectId).stream().map(mapper::toDomain).toList(); }
}
