package com.company.scopery.modules.traceability.screenprocessitem.infrastructure.mapper;

import com.company.scopery.modules.traceability.screenprocessitem.domain.enums.RegistryScreenProcessItemStatus;
import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItem;
import com.company.scopery.modules.traceability.screenprocessitem.infrastructure.persistence.RegistryScreenProcessItemJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryScreenProcessItemPersistenceMapper {

    public RegistryScreenProcessItem toDomain(RegistryScreenProcessItemJpaEntity e) {
        return new RegistryScreenProcessItem(
                e.getId(),
                e.getScreenId(),
                e.getWorkspaceId(),
                e.getModeId(),
                e.getTargetFieldId(),
                e.getTitle(),
                e.getContent(),
                e.getSourceTable(),
                e.getConditionNote(),
                e.getDisplayOrder(),
                RegistryScreenProcessItemStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public RegistryScreenProcessItemJpaEntity toJpaEntity(RegistryScreenProcessItem d) {
        RegistryScreenProcessItemJpaEntity e = new RegistryScreenProcessItemJpaEntity();
        e.setId(d.id());
        e.setScreenId(d.screenId());
        e.setWorkspaceId(d.workspaceId());
        e.setModeId(d.modeId());
        e.setTargetFieldId(d.targetFieldId());
        e.setTitle(d.title());
        e.setContent(d.content());
        e.setSourceTable(d.sourceTable());
        e.setConditionNote(d.conditionNote());
        e.setDisplayOrder(d.displayOrder());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
