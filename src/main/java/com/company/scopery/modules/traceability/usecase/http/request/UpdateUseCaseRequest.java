package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUseCaseRequest(
        @NotBlank String name,
        String goal,
        String primaryActorName,
        String triggerText,
        @NotBlank String status
) {}
