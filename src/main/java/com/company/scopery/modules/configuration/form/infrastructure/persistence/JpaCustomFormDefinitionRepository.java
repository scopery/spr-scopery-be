package com.company.scopery.modules.configuration.form.infrastructure.persistence;
import com.company.scopery.modules.configuration.form.domain.model.*;
import com.company.scopery.modules.configuration.form.infrastructure.mapper.FormPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCustomFormDefinitionRepository implements CustomFormDefinitionRepository {
    private final SpringDataCustomFormDefinitionJpaRepository springData; private final FormPersistenceMapper mapper;
    public JpaCustomFormDefinitionRepository(SpringDataCustomFormDefinitionJpaRepository springData, FormPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public CustomFormDefinition save(CustomFormDefinition f) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(f))); }
    @Override public Optional<CustomFormDefinition> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<CustomFormDefinition> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByCode(UUID workspaceId, String formCode) { return springData.existsByWorkspaceIdAndFormCode(workspaceId, formCode); }
}
