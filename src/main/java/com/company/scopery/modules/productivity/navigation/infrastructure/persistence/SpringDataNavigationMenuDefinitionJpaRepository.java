package com.company.scopery.modules.productivity.navigation.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataNavigationMenuDefinitionJpaRepository extends JpaRepository<NavigationMenuDefinitionJpaEntity, UUID> {
    List<NavigationMenuDefinitionJpaEntity> findByEnabledTrueOrderBySortOrderAsc();
}
