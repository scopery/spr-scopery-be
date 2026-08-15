package com.company.scopery.modules.traceability.apiendpoint.application.response;

import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;
import java.time.Instant;
import java.util.UUID;

public record RegistryApiEndpointResponse(UUID id, UUID applicationId, String method,
                                           String pathPattern, String name, String description,
                                           String requestParamsJson, String responseSchemaJson,
                                           String status, Instant createdAt) {
    public static RegistryApiEndpointResponse from(RegistryApiEndpoint e) {
        return new RegistryApiEndpointResponse(e.id(), e.applicationId(), e.method(),
                e.pathPattern(), e.name(), e.description(),
                e.requestParamsJson(), e.responseSchemaJson(),
                e.status().name(), e.createdAt());
    }
}
