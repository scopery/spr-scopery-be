package com.company.scopery.modules.traceability.functionapi.application.response;

import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApi;

import java.time.Instant;
import java.util.UUID;

public record FunctionApiResponse(UUID functionId, UUID apiEndpointId, String note, Instant createdAt) {

    public static FunctionApiResponse from(FunctionApi d) {
        return new FunctionApiResponse(d.functionId(), d.apiEndpointId(), d.note(), d.createdAt());
    }
}
