package com.company.scopery.modules.configuration.fieldvalue.infrastructure.persistence;
import com.company.scopery.modules.configuration.fieldvalue.domain.model.*;
import com.company.scopery.modules.configuration.fieldvalue.infrastructure.mapper.CustomFieldValuePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaCustomFieldValueRepository implements CustomFieldValueRepository {
    private final SpringDataCustomFieldValueJpaRepository springData; private final CustomFieldValuePersistenceMapper mapper;
    public JpaCustomFieldValueRepository(SpringDataCustomFieldValueJpaRepository springData, CustomFieldValuePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public CustomFieldValue save(CustomFieldValue v) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(v))); }
    @Override public Optional<CustomFieldValue> findByFieldAndTarget(UUID fieldId, UUID targetId) {
        return springData.findByCustomFieldDefinitionIdAndTargetId(fieldId, targetId).map(mapper::toDomain);
    }
    @Override public List<CustomFieldValue> findByWorkspaceObjectTarget(UUID workspaceId, String objectType, UUID targetId) {
        return springData.findByWorkspaceIdAndObjectTypeCodeAndTargetId(workspaceId, objectType, targetId).stream().map(mapper::toDomain).toList();
    }
}
