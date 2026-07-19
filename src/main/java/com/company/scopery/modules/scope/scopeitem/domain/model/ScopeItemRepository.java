package com.company.scopery.modules.scope.scopeitem.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ScopeItemRepository {
    ScopeItem save(ScopeItem item);
    Optional<ScopeItem> findByIdAndProjectId(UUID id, UUID projectId);
    List<ScopeItem> findByScopePackageId(UUID scopePackageId);
    List<ScopeItem> findByProjectId(UUID projectId);
}
