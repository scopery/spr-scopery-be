package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkLinkRequirementFunctionRequest(
        @NotEmpty @Size(max = 20) List<@NotNull String> requirementIds
) {}
