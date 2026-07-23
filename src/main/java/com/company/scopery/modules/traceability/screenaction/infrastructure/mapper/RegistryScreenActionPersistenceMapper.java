package com.company.scopery.modules.traceability.screenaction.infrastructure.mapper;
import com.company.scopery.modules.traceability.screenaction.domain.enums.RegistryScreenActionStatus;
import com.company.scopery.modules.traceability.screenaction.domain.model.RegistryScreenAction;
import com.company.scopery.modules.traceability.screenaction.infrastructure.persistence.RegistryScreenActionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RegistryScreenActionPersistenceMapper {
    public RegistryScreenAction toDomain(RegistryScreenActionJpaEntity e) {
        return new RegistryScreenAction(e.getId(), e.getScreenId(), e.getWorkspaceId(), e.getActionCode(),
                e.getName(), e.getActionType(), e.getDescription(), e.getDisplayOrder(),
                RegistryScreenActionStatus.valueOf(e.getStatus()),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RegistryScreenActionJpaEntity toJpaEntity(RegistryScreenAction d) {
        RegistryScreenActionJpaEntity e = new RegistryScreenActionJpaEntity();
        e.setId(d.id()); e.setScreenId(d.screenId()); e.setWorkspaceId(d.workspaceId());
        e.setActionCode(d.actionCode()); e.setName(d.name()); e.setActionType(d.actionType());
        e.setDescription(d.description()); e.setDisplayOrder(d.displayOrder());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
