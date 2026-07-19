package com.company.scopery.modules.productivity.navigation.infrastructure.persistence;
import com.company.scopery.modules.productivity.navigation.domain.model.*;
import com.company.scopery.modules.productivity.navigation.infrastructure.mapper.NavigationPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaNavigationMenuDefinitionRepository implements NavigationMenuDefinitionRepository {
    private final SpringDataNavigationMenuDefinitionJpaRepository springData; private final NavigationPersistenceMapper mapper;
    public JpaNavigationMenuDefinitionRepository(SpringDataNavigationMenuDefinitionJpaRepository springData, NavigationPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public List<NavigationMenuDefinition> findEnabled() { return springData.findByEnabledTrueOrderBySortOrderAsc().stream().map(mapper::toDomain).toList(); }
    @Override public NavigationMenuDefinition save(NavigationMenuDefinition m) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(m))); }
}
