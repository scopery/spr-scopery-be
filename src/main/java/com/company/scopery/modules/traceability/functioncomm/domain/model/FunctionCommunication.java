package com.company.scopery.modules.traceability.functioncomm.domain.model;

import java.time.Instant;
import java.util.UUID;

public record FunctionCommunication(UUID functionId, UUID communicationId, String note, Instant createdAt) {

    public static FunctionCommunication create(UUID functionId, UUID communicationId, String note) {
        return new FunctionCommunication(functionId, communicationId, note, Instant.now());
    }
}
