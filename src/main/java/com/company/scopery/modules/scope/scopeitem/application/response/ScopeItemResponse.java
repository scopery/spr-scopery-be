package com.company.scopery.modules.scope.scopeitem.application.response;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItem;
import java.time.Instant; import java.util.UUID;
public record ScopeItemResponse(UUID id, UUID scopePackageId, UUID projectId, String type, String code, String title,
        String description, boolean inScope, boolean outOfScope, String priority, boolean acceptanceRequired,
        String status, Integer sortOrder, Instant createdAt, Instant updatedAt) {
    public static ScopeItemResponse from(ScopeItem i) {
        return new ScopeItemResponse(i.id(), i.scopePackageId(), i.projectId(), i.type().name(), i.code(), i.title(),
                i.description(), i.inScope(), i.outOfScope(), i.priority(), i.acceptanceRequired(), i.status().name(),
                i.sortOrder(), i.createdAt(), i.updatedAt());
    }
}
