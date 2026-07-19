package com.company.scopery.modules.productivity.navigation.infrastructure.mapper;
import com.company.scopery.modules.productivity.navigation.domain.model.*;
import com.company.scopery.modules.productivity.navigation.infrastructure.persistence.*;
import org.springframework.stereotype.Component;
@Component
public class NavigationPersistenceMapper {
    public UserNavigationPreference toDomain(UserNavigationPreferenceJpaEntity e) {
        return new UserNavigationPreference(e.getId(), e.getWorkspaceId(), e.getUserId(), e.getPreferenceJson(), e.getDefaultLandingRoute(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public UserNavigationPreferenceJpaEntity toJpa(UserNavigationPreference d) {
        UserNavigationPreferenceJpaEntity e = new UserNavigationPreferenceJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setUserId(d.userId()); e.setPreferenceJson(d.preferenceJson());
        e.setDefaultLandingRoute(d.defaultLandingRoute()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
    public NavigationMenuDefinition toDomain(NavigationMenuDefinitionJpaEntity e) {
        return new NavigationMenuDefinition(e.getId(), e.getCode(), e.getParentCode(), e.getLabel(), e.getMenuType(), e.getRoutePath(),
                e.getRequiredPermission(), e.getContextType(), e.getSortOrder(), e.isEnabled(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public NavigationMenuDefinitionJpaEntity toJpa(NavigationMenuDefinition d) {
        NavigationMenuDefinitionJpaEntity e = new NavigationMenuDefinitionJpaEntity();
        e.setId(d.id()); e.setCode(d.code()); e.setParentCode(d.parentCode()); e.setLabel(d.label()); e.setMenuType(d.menuType());
        e.setRoutePath(d.routePath()); e.setRequiredPermission(d.requiredPermission()); e.setContextType(d.contextType());
        e.setSortOrder(d.sortOrder()); e.setEnabled(d.enabled()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
