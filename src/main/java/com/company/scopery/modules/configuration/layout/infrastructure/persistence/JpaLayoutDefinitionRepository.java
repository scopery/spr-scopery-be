package com.company.scopery.modules.configuration.layout.infrastructure.persistence;
import com.company.scopery.modules.configuration.layout.domain.model.*;
import com.company.scopery.modules.configuration.layout.infrastructure.mapper.LayoutDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaLayoutDefinitionRepository implements LayoutDefinitionRepository {
    private final SpringDataLayoutDefinitionJpaRepository springData; private final LayoutDefinitionPersistenceMapper mapper;
    public JpaLayoutDefinitionRepository(SpringDataLayoutDefinitionJpaRepository springData, LayoutDefinitionPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public LayoutDefinition save(LayoutDefinition l) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(l))); }
    @Override public Optional<LayoutDefinition> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<LayoutDefinition> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByNameAsc(workspaceId).stream().map(mapper::toDomain).toList(); }
}
