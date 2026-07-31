package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotBlank;

public record AddSupportingFunctionRequest(
        @NotBlank String functionId
) {}
