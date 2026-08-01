package com.company.scopery.modules.traceability.functioncomm.application.response;

import com.company.scopery.modules.traceability.functioncomm.domain.model.FunctionCommunication;

import java.time.Instant;
import java.util.UUID;

public record FunctionCommunicationResponse(
        UUID functionId,
        UUID communicationId,
        String note,
        Instant createdAt
) {
    public static FunctionCommunicationResponse from(FunctionCommunication d) {
        return new FunctionCommunicationResponse(d.functionId(), d.communicationId(), d.note(), d.createdAt());
    }
}
