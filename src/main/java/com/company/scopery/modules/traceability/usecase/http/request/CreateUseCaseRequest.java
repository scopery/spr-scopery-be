package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateUseCaseRequest(
        @NotBlank String primaryFunctionId,
        @NotBlank String key,
        @NotBlank String name,
        String goal,
        String primaryActorName,
        String triggerText
) {}
