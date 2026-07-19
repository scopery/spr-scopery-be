package com.company.scopery.modules.productivity.savedview.infrastructure.mapper;
import com.company.scopery.modules.productivity.savedview.domain.model.SavedView;
import com.company.scopery.modules.productivity.savedview.infrastructure.persistence.SavedViewJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SavedViewPersistenceMapper {
    public SavedView toDomain(SavedViewJpaEntity e) {
        return new SavedView(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getOwnerUserId(), e.getTargetType(), e.getName(),
                e.getViewConfigJson(), e.getFiltersJson(), e.getSortJson(), e.getColumnsJson(), e.getDisplayMode(),
                e.getVisibility(), e.isDefaultFlag(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public SavedViewJpaEntity toJpaEntity(SavedView d) {
        SavedViewJpaEntity e = new SavedViewJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setOwnerUserId(d.ownerUserId());
        e.setTargetType(d.targetType()); e.setName(d.name()); e.setViewConfigJson(d.viewConfigJson()); e.setFiltersJson(d.filtersJson());
        e.setSortJson(d.sortJson()); e.setColumnsJson(d.columnsJson()); e.setDisplayMode(d.displayMode());
        e.setVisibility(d.visibility()); e.setDefaultFlag(d.defaultFlag()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
