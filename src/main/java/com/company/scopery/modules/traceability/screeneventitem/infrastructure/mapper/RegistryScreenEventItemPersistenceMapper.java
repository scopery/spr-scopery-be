package com.company.scopery.modules.traceability.screeneventitem.infrastructure.mapper;

import com.company.scopery.modules.traceability.screeneventitem.domain.enums.RegistryScreenEventItemStatus;
import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItem;
import com.company.scopery.modules.traceability.screeneventitem.infrastructure.persistence.RegistryScreenEventItemJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryScreenEventItemPersistenceMapper {

    public RegistryScreenEventItem toDomain(RegistryScreenEventItemJpaEntity e) {
        return new RegistryScreenEventItem(
                e.getId(),
                e.getScreenId(),
                e.getWorkspaceId(),
                e.getModeId(),
                e.getTriggerFieldId(),
                e.getTriggerActionCode(),
                e.getTitle(),
                e.getContent(),
                e.getConditionNote(),
                e.getTargetScreenId(),
                e.getTargetModeCode(),
                e.getDisplayOrder(),
                RegistryScreenEventItemStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public RegistryScreenEventItemJpaEntity toJpaEntity(RegistryScreenEventItem d) {
        RegistryScreenEventItemJpaEntity e = new RegistryScreenEventItemJpaEntity();
        e.setId(d.id());
        e.setScreenId(d.screenId());
        e.setWorkspaceId(d.workspaceId());
        e.setModeId(d.modeId());
        e.setTriggerFieldId(d.triggerFieldId());
        e.setTriggerActionCode(d.triggerActionCode());
        e.setTitle(d.title());
        e.setContent(d.content());
        e.setConditionNote(d.conditionNote());
        e.setTargetScreenId(d.targetScreenId());
        e.setTargetModeCode(d.targetModeCode());
        e.setDisplayOrder(d.displayOrder());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
