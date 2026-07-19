package com.company.scopery.modules.productivity.savedsearch.infrastructure.persistence;
import com.company.scopery.modules.productivity.savedsearch.domain.model.*;
import com.company.scopery.modules.productivity.savedsearch.infrastructure.mapper.SavedSearchPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaSavedSearchRepository implements SavedSearchRepository {
    private final SpringDataSavedSearchJpaRepository springData; private final SavedSearchPersistenceMapper mapper;
    public JpaSavedSearchRepository(SpringDataSavedSearchJpaRepository springData, SavedSearchPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public SavedSearch save(SavedSearch s) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(s))); }
    @Override public Optional<SavedSearch> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<SavedSearch> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList(); }
}
