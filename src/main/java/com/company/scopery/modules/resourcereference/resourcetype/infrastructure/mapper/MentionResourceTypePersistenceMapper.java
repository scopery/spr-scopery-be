package com.company.scopery.modules.resourcereference.resourcetype.infrastructure.mapper;

import com.company.scopery.modules.resourcereference.resourcetype.domain.model.MentionResourceTypeDefinition;
import com.company.scopery.modules.resourcereference.resourcetype.infrastructure.persistence.MentionResourceTypeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MentionResourceTypePersistenceMapper {

    public MentionResourceTypeDefinition toDomain(MentionResourceTypeJpaEntity e) {
        return new MentionResourceTypeDefinition(e.getId(), e.getCode(), e.getDisplayName(),
                e.getDescription(), e.isSystem(), e.isEnabled(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public MentionResourceTypeJpaEntity toJpaEntity(MentionResourceTypeDefinition d) {
        MentionResourceTypeJpaEntity e = new MentionResourceTypeJpaEntity();
        e.setId(d.id());
        e.setCode(d.code());
        e.setDisplayName(d.displayName());
        e.setDescription(d.description());
        e.setSystem(d.isSystem());
        e.setEnabled(d.enabled());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
