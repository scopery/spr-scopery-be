package com.company.scopery.modules.traceability.screen.infrastructure.mapper;

import com.company.scopery.modules.traceability.screen.domain.enums.RegistryScreenStatus;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;
import com.company.scopery.modules.traceability.screen.infrastructure.persistence.RegistryScreenJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryScreenPersistenceMapper {
    public RegistryScreen toDomain(RegistryScreenJpaEntity e) {
        return new RegistryScreen(e.getId(), e.getApplicationId(), e.getProjectId(), e.getCode(), e.getName(),
                e.getRoutePath(), e.getStatus() != null ? RegistryScreenStatus.valueOf(e.getStatus()) : RegistryScreenStatus.ACTIVE,
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public RegistryScreenJpaEntity toJpaEntity(RegistryScreen d) {
        RegistryScreenJpaEntity e = new RegistryScreenJpaEntity();
        e.setId(d.id()); e.setApplicationId(d.applicationId()); e.setProjectId(d.projectId());
        e.setCode(d.code()); e.setName(d.name()); e.setRoutePath(d.routePath());
        e.setStatus(d.status().name()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
