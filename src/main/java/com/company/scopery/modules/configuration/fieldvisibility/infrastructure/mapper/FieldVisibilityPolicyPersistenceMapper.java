package com.company.scopery.modules.configuration.fieldvisibility.infrastructure.mapper;
import com.company.scopery.modules.configuration.fieldvisibility.domain.model.FieldVisibilityPolicy;
import com.company.scopery.modules.configuration.fieldvisibility.infrastructure.persistence.FieldVisibilityPolicyJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class FieldVisibilityPolicyPersistenceMapper {
    public FieldVisibilityPolicy toDomain(FieldVisibilityPolicyJpaEntity e) {
        return new FieldVisibilityPolicy(e.getId(), e.getWorkspaceId(), e.getCustomFieldDefinitionId(), e.getAudienceType(), e.isVisible(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public FieldVisibilityPolicyJpaEntity toJpa(FieldVisibilityPolicy d) {
        FieldVisibilityPolicyJpaEntity e = new FieldVisibilityPolicyJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setCustomFieldDefinitionId(d.customFieldDefinitionId());
        e.setAudienceType(d.audienceType()); e.setVisible(d.visible());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
