package com.company.scopery.modules.configuration.statusset.infrastructure.persistence;
import com.company.scopery.modules.configuration.statusset.domain.model.*;
import com.company.scopery.modules.configuration.statusset.infrastructure.mapper.StatusSetPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaStatusSetRepository implements StatusSetRepository {
    private final SpringDataStatusSetJpaRepository springData; private final StatusSetPersistenceMapper mapper;
    public JpaStatusSetRepository(SpringDataStatusSetJpaRepository springData, StatusSetPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public StatusSet save(StatusSet s) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(s))); }
    @Override public Optional<StatusSet> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<StatusSet> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByNameAsc(workspaceId).stream().map(mapper::toDomain).toList(); }
}
