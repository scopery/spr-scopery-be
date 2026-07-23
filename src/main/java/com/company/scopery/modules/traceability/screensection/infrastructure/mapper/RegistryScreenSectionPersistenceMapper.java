package com.company.scopery.modules.traceability.screensection.infrastructure.mapper;
import com.company.scopery.modules.traceability.screensection.domain.enums.RegistryScreenSectionStatus;
import com.company.scopery.modules.traceability.screensection.domain.model.RegistryScreenSection;
import com.company.scopery.modules.traceability.screensection.infrastructure.persistence.RegistryScreenSectionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RegistryScreenSectionPersistenceMapper {
    public RegistryScreenSection toDomain(RegistryScreenSectionJpaEntity e) {
        return new RegistryScreenSection(e.getId(), e.getScreenId(), e.getWorkspaceId(), e.getName(), e.getDescription(),
                e.getDisplayOrder(), RegistryScreenSectionStatus.valueOf(e.getStatus()),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RegistryScreenSectionJpaEntity toJpaEntity(RegistryScreenSection d) {
        RegistryScreenSectionJpaEntity e = new RegistryScreenSectionJpaEntity();
        e.setId(d.id()); e.setScreenId(d.screenId()); e.setWorkspaceId(d.workspaceId());
        e.setName(d.name()); e.setDescription(d.description()); e.setDisplayOrder(d.displayOrder());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
