package com.company.scopery.modules.resourcereference.batchresolve.application.response;

import java.util.UUID;

public record ResolvedResourceResponse(
        String resourceType,
        UUID resourceId,
        String displayName,
        String status
) {
    public static ResolvedResourceResponse resolved(String type, UUID id, String displayName) {
        return new ResolvedResourceResponse(type, id, displayName, "RESOLVED");
    }

    public static ResolvedResourceResponse accessRevoked(String type, UUID id) {
        return new ResolvedResourceResponse(type, id, null, "ACCESS_REVOKED");
    }

    public static ResolvedResourceResponse notFound(String type, UUID id) {
        return new ResolvedResourceResponse(type, id, null, "NOT_FOUND");
    }
}
