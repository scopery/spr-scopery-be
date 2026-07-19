package com.company.scopery.modules.configuration.validation.infrastructure.persistence;
import com.company.scopery.modules.configuration.validation.domain.model.*;
import com.company.scopery.modules.configuration.validation.infrastructure.mapper.CustomFieldValidationRulePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCustomFieldValidationRuleRepository implements CustomFieldValidationRuleRepository {
    private final SpringDataCustomFieldValidationRuleJpaRepository springData; private final CustomFieldValidationRulePersistenceMapper mapper;
    public JpaCustomFieldValidationRuleRepository(SpringDataCustomFieldValidationRuleJpaRepository springData, CustomFieldValidationRulePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public CustomFieldValidationRule save(CustomFieldValidationRule r) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(r))); }
    @Override public Optional<CustomFieldValidationRule> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<CustomFieldValidationRule> findByFieldId(UUID fieldId) { return springData.findByCustomFieldDefinitionId(fieldId).stream().map(mapper::toDomain).toList(); }
    @Override public void delete(UUID id) { springData.deleteById(id); }
}
