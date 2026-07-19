package com.company.scopery.modules.configuration.customfield.infrastructure.persistence;
import com.company.scopery.modules.configuration.customfield.domain.model.*;
import com.company.scopery.modules.configuration.customfield.infrastructure.mapper.CustomFieldDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCustomFieldDefinitionRepository implements CustomFieldDefinitionRepository {
    private final SpringDataCustomFieldDefinitionJpaRepository springData; private final CustomFieldDefinitionPersistenceMapper mapper;
    public JpaCustomFieldDefinitionRepository(SpringDataCustomFieldDefinitionJpaRepository springData, CustomFieldDefinitionPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public CustomFieldDefinition save(CustomFieldDefinition d) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(d))); }
    @Override public Optional<CustomFieldDefinition> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public boolean existsByKey(UUID workspaceId, String objectTypeCode, String fieldKey) { return springData.existsByWorkspaceIdAndObjectTypeCodeAndFieldKey(workspaceId, objectTypeCode, fieldKey); }
    @Override public List<CustomFieldDefinition> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList(); }
}
