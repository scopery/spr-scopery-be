package com.company.scopery.modules.productivity.navigation.application.response;
import com.company.scopery.modules.productivity.navigation.domain.model.NavigationMenuDefinition;
import java.util.UUID;
public record NavigationMenuResponse(UUID id, String code, String parentCode, String label, String routePath, int sortOrder) {
    public static NavigationMenuResponse from(NavigationMenuDefinition m) {
        return new NavigationMenuResponse(m.id(), m.code(), m.parentCode(), m.label(), m.routePath(), m.sortOrder());
    }
}
