package com.company.scopery.modules.scope.scopepackage.application.response;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackage;
import java.time.Instant; import java.util.UUID;
public record ScopePackageResponse(UUID id, UUID projectId, String code, String name, String description, String status,
        boolean currentFlag, Instant approvedAt, UUID approvedBy, Instant createdAt) {
    public static ScopePackageResponse from(ScopePackage p) {
        return new ScopePackageResponse(p.id(), p.projectId(), p.code(), p.name(), p.description(), p.status().name(),
                p.currentFlag(), p.approvedAt(), p.approvedBy(), p.createdAt());
    }
}
