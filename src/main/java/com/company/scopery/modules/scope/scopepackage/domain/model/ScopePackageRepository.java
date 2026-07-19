package com.company.scopery.modules.scope.scopepackage.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ScopePackageRepository {
    ScopePackage save(ScopePackage pkg);
    Optional<ScopePackage> findByIdAndProjectId(UUID id, UUID projectId);
    List<ScopePackage> findByProjectId(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
    void clearCurrentFlag(UUID projectId);
}
