package com.company.scopery.modules.governance.objecttype.infrastructure.mapper;
import com.company.scopery.modules.governance.objecttype.domain.model.GovernedObjectType;
import com.company.scopery.modules.governance.objecttype.infrastructure.persistence.GovernedObjectTypeJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class GovernedObjectTypePersistenceMapper {
    public GovernedObjectType toDomain(GovernedObjectTypeJpaEntity e) {
        return new GovernedObjectType(e.getId(), e.getObjectTypeCode(), e.isOwnershipSupported(),
                e.isVersioningSupported(), e.isLockingSupported(), e.isRestoreSupported(),
                e.isEnabled(), e.getVersion() != null ? e.getVersion() : 0, e.getCreatedAt(), e.getUpdatedAt());
    }
    public GovernedObjectTypeJpaEntity toJpa(GovernedObjectType d) {
        var e = new GovernedObjectTypeJpaEntity();
        e.setId(d.id()); e.setObjectTypeCode(d.objectTypeCode());
        e.setOwnershipSupported(d.ownershipSupported()); e.setVersioningSupported(d.versioningSupported());
        e.setLockingSupported(d.lockingSupported()); e.setRestoreSupported(d.restoreSupported());
        e.setEnabled(d.enabled());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
