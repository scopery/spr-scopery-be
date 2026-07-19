package com.company.scopery.modules.configuration.fieldvisibility.infrastructure.persistence;
import com.company.scopery.modules.configuration.fieldvisibility.domain.model.*;
import com.company.scopery.modules.configuration.fieldvisibility.infrastructure.mapper.FieldVisibilityPolicyPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaFieldVisibilityPolicyRepository implements FieldVisibilityPolicyRepository {
    private final SpringDataFieldVisibilityPolicyJpaRepository springData; private final FieldVisibilityPolicyPersistenceMapper mapper;
    public JpaFieldVisibilityPolicyRepository(SpringDataFieldVisibilityPolicyJpaRepository springData, FieldVisibilityPolicyPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public FieldVisibilityPolicy save(FieldVisibilityPolicy p) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(p))); }
    @Override public Optional<FieldVisibilityPolicy> findByFieldAndAudience(UUID fieldId, String audienceType) { return springData.findByCustomFieldDefinitionIdAndAudienceType(fieldId, audienceType).map(mapper::toDomain); }
    @Override public List<FieldVisibilityPolicy> findByFieldId(UUID workspaceId, UUID fieldId) { return springData.findByWorkspaceIdAndCustomFieldDefinitionId(workspaceId, fieldId).stream().map(mapper::toDomain).toList(); }
    @Override public List<FieldVisibilityPolicy> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
