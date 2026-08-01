package com.company.scopery.modules.traceability.functioncomm.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LinkFunctionCommunicationRequest(
        @NotNull UUID communicationId,
        String note
) {}
