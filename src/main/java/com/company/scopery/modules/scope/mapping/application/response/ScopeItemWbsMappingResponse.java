package com.company.scopery.modules.scope.mapping.application.response;
import com.company.scopery.modules.scope.mapping.domain.model.ScopeItemWbsMapping;
import java.time.Instant; import java.util.UUID;
public record ScopeItemWbsMappingResponse(UUID id, UUID scopeItemId, UUID projectId, UUID wbsNodeId,
        String mappingType, Instant archivedAt, Instant createdAt) {
    public static ScopeItemWbsMappingResponse from(ScopeItemWbsMapping m) {
        return new ScopeItemWbsMappingResponse(m.id(), m.scopeItemId(), m.projectId(), m.wbsNodeId(),
                m.mappingType().name(), m.archivedAt(), m.createdAt());
    }
}
