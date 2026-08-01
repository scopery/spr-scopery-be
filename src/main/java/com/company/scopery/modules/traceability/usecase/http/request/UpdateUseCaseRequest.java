package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUseCaseRequest(
        @NotBlank String name,
        String goal,
        String primaryActorName,
        String triggerText,
        @NotBlank String status,
        /** Optional — set/clear which Function is primary for this Use Case. */
        String primaryFunctionId
) {}
