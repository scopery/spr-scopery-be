package com.company.scopery.modules.scope.mapping.infrastructure.persistence;
import com.company.scopery.modules.scope.mapping.domain.model.ScopeItemWbsMapping;
import com.company.scopery.modules.scope.mapping.domain.model.ScopeItemWbsMappingRepository;
import com.company.scopery.modules.scope.mapping.infrastructure.mapper.ScopeItemWbsMappingPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaScopeItemWbsMappingRepository implements ScopeItemWbsMappingRepository {
    private final SpringDataScopeItemWbsMappingJpaRepository springData;
    private final ScopeItemWbsMappingPersistenceMapper mapper;
    public JpaScopeItemWbsMappingRepository(SpringDataScopeItemWbsMappingJpaRepository springData,
                                            ScopeItemWbsMappingPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public ScopeItemWbsMapping save(ScopeItemWbsMapping mapping) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(mapping)));
    }
    @Override public Optional<ScopeItemWbsMapping> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<ScopeItemWbsMapping> findActiveByScopeItemId(UUID scopeItemId) {
        return springData.findByScopeItemIdAndArchivedAtIsNullOrderByCreatedAtDesc(scopeItemId).stream()
                .map(mapper::toDomain).toList();
    }
    @Override public long countActiveByProjectId(UUID projectId) {
        return springData.countByProjectIdAndArchivedAtIsNull(projectId);
    }
}
