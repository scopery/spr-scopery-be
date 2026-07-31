package com.company.scopery.modules.traceability.requirement.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SetRequiresUseCaseRequest(
        @NotBlank
        @Pattern(regexp = "YES|NO|AUTO", message = "requiresUseCase must be YES, NO, or AUTO")
        String value
) {}
