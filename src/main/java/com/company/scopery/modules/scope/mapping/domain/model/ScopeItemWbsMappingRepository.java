package com.company.scopery.modules.scope.mapping.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ScopeItemWbsMappingRepository {
    ScopeItemWbsMapping save(ScopeItemWbsMapping mapping);
    Optional<ScopeItemWbsMapping> findByIdAndProjectId(UUID id, UUID projectId);
    List<ScopeItemWbsMapping> findActiveByScopeItemId(UUID scopeItemId);
    long countActiveByProjectId(UUID projectId);
}
