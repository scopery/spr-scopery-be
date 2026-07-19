package com.company.scopery.modules.scope.scopeitem.infrastructure.mapper;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemStatus;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemType;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItem;
import com.company.scopery.modules.scope.scopeitem.infrastructure.persistence.ScopeItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ScopeItemPersistenceMapper {
    public ScopeItem toDomain(ScopeItemJpaEntity e) {
        return new ScopeItem(e.getId(), e.getScopePackageId(), e.getProjectId(), e.getWorkspaceId(), e.getSourceQuoteLineId(),
                e.getSourceChangeRequestId(), e.getParentScopeItemId(), ScopeItemType.valueOf(e.getType()), e.getCode(),
                e.getTitle(), e.getDescription(), e.isInScope(), e.isOutOfScope(), e.getPriority(), e.isAcceptanceRequired(),
                ScopeItemStatus.valueOf(e.getStatus()), e.getSortOrder(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ScopeItemJpaEntity toJpaEntity(ScopeItem d) {
        ScopeItemJpaEntity e = new ScopeItemJpaEntity();
        e.setId(d.id()); e.setScopePackageId(d.scopePackageId()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId());
        e.setSourceQuoteLineId(d.sourceQuoteLineId()); e.setSourceChangeRequestId(d.sourceChangeRequestId());
        e.setParentScopeItemId(d.parentScopeItemId()); e.setType(d.type().name()); e.setCode(d.code()); e.setTitle(d.title());
        e.setDescription(d.description()); e.setInScope(d.inScope()); e.setOutOfScope(d.outOfScope()); e.setPriority(d.priority());
        e.setAcceptanceRequired(d.acceptanceRequired()); e.setStatus(d.status().name()); e.setSortOrder(d.sortOrder());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
