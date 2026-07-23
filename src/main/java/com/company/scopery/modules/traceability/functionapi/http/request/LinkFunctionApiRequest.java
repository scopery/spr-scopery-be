package com.company.scopery.modules.traceability.functionapi.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LinkFunctionApiRequest(@NotNull UUID apiEndpointId, String note) {}
