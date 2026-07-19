package com.company.scopery.modules.scope.mapping.domain.model;
import com.company.scopery.modules.scope.mapping.domain.enums.MappingType;
import java.time.Instant; import java.util.UUID;
public record ScopeItemWbsMapping(UUID id, UUID scopeItemId, UUID projectId, UUID wbsNodeId, MappingType mappingType,
        Instant archivedAt, UUID archivedBy, int version, Instant createdAt) {
    public static ScopeItemWbsMapping create(UUID scopeItemId, UUID projectId, UUID wbsNodeId, MappingType mappingType) {
        return new ScopeItemWbsMapping(UUID.randomUUID(), scopeItemId, projectId, wbsNodeId, mappingType,
                null, null, 0, Instant.now());
    }
    public ScopeItemWbsMapping archive(UUID actorId) {
        if (archivedAt != null) throw new IllegalStateException("Mapping already archived");
        return new ScopeItemWbsMapping(id, scopeItemId, projectId, wbsNodeId, mappingType, Instant.now(), actorId, version, createdAt);
    }
    public boolean isActive() { return archivedAt == null; }
}
