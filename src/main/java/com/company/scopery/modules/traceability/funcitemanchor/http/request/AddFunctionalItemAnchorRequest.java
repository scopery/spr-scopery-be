package com.company.scopery.modules.traceability.funcitemanchor.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddFunctionalItemAnchorRequest(
        @NotBlank String nodeType,
        @NotNull UUID nodeId,
        String note
) {}
