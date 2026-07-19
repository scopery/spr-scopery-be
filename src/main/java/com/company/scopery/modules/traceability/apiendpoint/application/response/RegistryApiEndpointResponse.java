package com.company.scopery.modules.traceability.apiendpoint.application.response;

import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;
import java.time.Instant;
import java.util.UUID;

public record RegistryApiEndpointResponse(UUID id, UUID applicationId, String method,
                                           String pathPattern, String name, String status, Instant createdAt) {
    public static RegistryApiEndpointResponse from(RegistryApiEndpoint e) {
        return new RegistryApiEndpointResponse(e.id(), e.applicationId(), e.method(),
                e.pathPattern(), e.name(), e.status().name(), e.createdAt());
    }
}
