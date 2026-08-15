package com.company.scopery.modules.traceability.apiendpoint.domain.model;

import com.company.scopery.modules.traceability.apiendpoint.domain.enums.RegistryApiEndpointStatus;
import java.time.Instant;
import java.util.UUID;

public record RegistryApiEndpoint(UUID id, UUID applicationId, UUID projectId, String method,
                                   String pathPattern, String name, String description,
                                   String requestParamsJson, String responseSchemaJson,
                                   RegistryApiEndpointStatus status, int version, Instant createdAt) {
    public static RegistryApiEndpoint create(UUID applicationId, UUID projectId, String method, String pathPattern,
                                              String name, String description,
                                              String requestParamsJson, String responseSchemaJson) {
        return new RegistryApiEndpoint(UUID.randomUUID(), applicationId, projectId, method, pathPattern,
                name, description, requestParamsJson, responseSchemaJson,
                RegistryApiEndpointStatus.ACTIVE, 0, null);
    }
    public RegistryApiEndpoint withUpdated(String method, String pathPattern, String name,
                                            String description, String requestParamsJson, String responseSchemaJson) {
        return new RegistryApiEndpoint(id, applicationId, projectId, method, pathPattern, name,
                description, requestParamsJson, responseSchemaJson, status, version, createdAt);
    }
}
