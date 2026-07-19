package com.company.scopery.modules.trust.sensitivefield.infrastructure.mapper;

import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistry;
import com.company.scopery.modules.trust.sensitivefield.infrastructure.persistence.SensitiveFieldRegistryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SensitiveFieldRegistryPersistenceMapper {
    public SensitiveFieldRegistryJpaEntity toJpaEntity(SensitiveFieldRegistry d) {
        SensitiveFieldRegistryJpaEntity e = new SensitiveFieldRegistryJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setObjectTypeCode(d.objectTypeCode());
        e.setFieldPath(d.fieldPath());
        e.setClassification(d.classification());
        e.setMaskingStrategy(d.maskingStrategy());
        e.setAccessLoggingRequired(d.accessLoggingRequired());
        e.setSearchIndexAllowed(d.searchIndexAllowed());
        e.setExportAllowed(d.exportAllowed());
        e.setEnabled(d.enabled());
        e.setVersion(d.version());
        e.setCreatedAt(d.createdAt());
        return e;
    }

    public SensitiveFieldRegistry toDomain(SensitiveFieldRegistryJpaEntity e) {
        return new SensitiveFieldRegistry(
                e.getId(), e.getWorkspaceId(), e.getObjectTypeCode(), e.getFieldPath(), e.getClassification(),
                e.getMaskingStrategy(), e.isAccessLoggingRequired(), e.isSearchIndexAllowed(), e.isExportAllowed(),
                e.isEnabled(), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
}
