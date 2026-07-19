package com.company.scopery.modules.productivity.navigation.domain.model;
import java.time.Instant; import java.util.UUID;
public record NavigationMenuDefinition(UUID id, String code, String parentCode, String label, String menuType, String routePath,
        String requiredPermission, String contextType, int sortOrder, boolean enabled, int version, Instant createdAt, Instant updatedAt) {}
