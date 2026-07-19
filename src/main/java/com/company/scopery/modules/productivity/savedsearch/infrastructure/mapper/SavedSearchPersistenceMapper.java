package com.company.scopery.modules.productivity.savedsearch.infrastructure.mapper;
import com.company.scopery.modules.productivity.savedsearch.domain.model.SavedSearch;
import com.company.scopery.modules.productivity.savedsearch.infrastructure.persistence.SavedSearchJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SavedSearchPersistenceMapper {
    public SavedSearch toDomain(SavedSearchJpaEntity e) {
        return new SavedSearch(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getOwnerUserId(), e.getName(), e.getScope(), e.getQueryText(),
                e.getFiltersJson(), e.getSortJson(), e.getVisibility(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public SavedSearchJpaEntity toJpaEntity(SavedSearch d) {
        SavedSearchJpaEntity e = new SavedSearchJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setOwnerUserId(d.ownerUserId());
        e.setName(d.name()); e.setScope(d.scope()); e.setQueryText(d.queryText()); e.setFiltersJson(d.filtersJson());
        e.setSortJson(d.sortJson()); e.setVisibility(d.visibility()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
