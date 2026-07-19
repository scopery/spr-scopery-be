package com.company.scopery.modules.trust.sensitiveobject.infrastructure.mapper;

import com.company.scopery.modules.trust.sensitiveobject.domain.model.SensitiveObjectRegistry;
import com.company.scopery.modules.trust.sensitiveobject.infrastructure.persistence.SensitiveObjectRegistryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SensitiveObjectRegistryPersistenceMapper {
    public SensitiveObjectRegistryJpaEntity toJpaEntity(SensitiveObjectRegistry d) {
        SensitiveObjectRegistryJpaEntity e = new SensitiveObjectRegistryJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setObjectTypeCode(d.objectTypeCode());
        e.setClassification(d.classification());
        e.setAccessLoggingRequired(d.accessLoggingRequired());
        e.setExportReasonRequired(d.exportReasonRequired());
        e.setSearchIndexAllowed(d.searchIndexAllowed());
        e.setEnabled(d.enabled());
        e.setVersion(d.version());
        e.setCreatedAt(d.createdAt());
        return e;
    }

    public SensitiveObjectRegistry toDomain(SensitiveObjectRegistryJpaEntity e) {
        return new SensitiveObjectRegistry(
                e.getId(), e.getWorkspaceId(), e.getObjectTypeCode(), e.getClassification(),
                e.isAccessLoggingRequired(), e.isExportReasonRequired(), e.isSearchIndexAllowed(),
                e.isEnabled(), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
}
