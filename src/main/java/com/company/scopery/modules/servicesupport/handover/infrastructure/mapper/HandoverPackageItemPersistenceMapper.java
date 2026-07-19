package com.company.scopery.modules.servicesupport.handover.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.handover.domain.model.HandoverPackageItem;
import com.company.scopery.modules.servicesupport.handover.infrastructure.persistence.HandoverPackageItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class HandoverPackageItemPersistenceMapper {
    public HandoverPackageItemJpaEntity toJpa(HandoverPackageItem d) {
        var e = new HandoverPackageItemJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setHandoverPackageId(d.handoverPackageId());
        e.setItemType(d.itemType()); e.setTargetObjectType(d.targetObjectType()); e.setTargetObjectId(d.targetObjectId());
        e.setDocumentId(d.documentId()); e.setTitle(d.title()); e.setDescription(d.description());
        e.setClientVisible(d.clientVisible()); e.setSortOrder(d.sortOrder()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public HandoverPackageItem toDomain(HandoverPackageItemJpaEntity e) {
        return new HandoverPackageItem(e.getId(), e.getWorkspaceId(), e.getHandoverPackageId(), e.getItemType(),
                e.getTargetObjectType(), e.getTargetObjectId(), e.getDocumentId(), e.getTitle(), e.getDescription(),
                e.isClientVisible(), e.getSortOrder(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
