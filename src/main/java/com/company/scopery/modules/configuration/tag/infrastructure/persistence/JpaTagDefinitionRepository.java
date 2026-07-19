package com.company.scopery.modules.configuration.tag.infrastructure.persistence;
import com.company.scopery.modules.configuration.tag.domain.model.*;
import com.company.scopery.modules.configuration.tag.infrastructure.mapper.TagDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaTagDefinitionRepository implements TagDefinitionRepository {
    private final SpringDataTagDefinitionJpaRepository springData; private final TagDefinitionPersistenceMapper mapper;
    public JpaTagDefinitionRepository(SpringDataTagDefinitionJpaRepository springData, TagDefinitionPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public TagDefinition save(TagDefinition t) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(t))); }
    @Override public Optional<TagDefinition> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<TagDefinition> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByLabelAsc(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByCode(UUID workspaceId, String code) { return springData.existsByWorkspaceIdAndTagCode(workspaceId, code); }
}
