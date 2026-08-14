package com.company.scopery.modules.traceability.screenmode.infrastructure.mapper;

import com.company.scopery.modules.traceability.screenmode.domain.enums.RegistryScreenModeStatus;
import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenMode;
import com.company.scopery.modules.traceability.screenmode.infrastructure.persistence.RegistryScreenModeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryScreenModePersistenceMapper {

    public RegistryScreenMode toDomain(RegistryScreenModeJpaEntity e) {
        return new RegistryScreenMode(
                e.getId(),
                e.getScreenId(),
                e.getWorkspaceId(),
                e.getModeCode(),
                e.getName(),
                e.getDisplayOrder(),
                RegistryScreenModeStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public RegistryScreenModeJpaEntity toJpaEntity(RegistryScreenMode d) {
        RegistryScreenModeJpaEntity e = new RegistryScreenModeJpaEntity();
        e.setId(d.id());
        e.setScreenId(d.screenId());
        e.setWorkspaceId(d.workspaceId());
        e.setModeCode(d.modeCode());
        e.setName(d.name());
        e.setDisplayOrder(d.displayOrder());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
