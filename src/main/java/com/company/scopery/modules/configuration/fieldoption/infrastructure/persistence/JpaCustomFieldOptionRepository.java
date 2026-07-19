package com.company.scopery.modules.configuration.fieldoption.infrastructure.persistence;
import com.company.scopery.modules.configuration.fieldoption.domain.model.*;
import com.company.scopery.modules.configuration.fieldoption.infrastructure.mapper.CustomFieldOptionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCustomFieldOptionRepository implements CustomFieldOptionRepository {
    private final SpringDataCustomFieldOptionJpaRepository springData; private final CustomFieldOptionPersistenceMapper mapper;
    public JpaCustomFieldOptionRepository(SpringDataCustomFieldOptionJpaRepository springData, CustomFieldOptionPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public CustomFieldOption save(CustomFieldOption o) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(o))); }
    @Override public Optional<CustomFieldOption> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<CustomFieldOption> findByFieldId(UUID fieldId) { return springData.findByCustomFieldDefinitionIdOrderBySortOrderAsc(fieldId).stream().map(mapper::toDomain).toList(); }
}
