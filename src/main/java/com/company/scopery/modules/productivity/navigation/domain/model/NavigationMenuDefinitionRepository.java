package com.company.scopery.modules.productivity.navigation.domain.model;
import java.util.List;
public interface NavigationMenuDefinitionRepository {
    List<NavigationMenuDefinition> findEnabled();
    NavigationMenuDefinition save(NavigationMenuDefinition m);
}
