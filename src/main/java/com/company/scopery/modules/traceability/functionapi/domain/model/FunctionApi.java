package com.company.scopery.modules.traceability.functionapi.domain.model;

import java.time.Instant;
import java.util.UUID;

public record FunctionApi(UUID functionId, UUID apiEndpointId, String note, Instant createdAt) {

    public static FunctionApi create(UUID functionId, UUID apiEndpointId, String note) {
        return new FunctionApi(functionId, apiEndpointId, note, Instant.now());
    }
}
