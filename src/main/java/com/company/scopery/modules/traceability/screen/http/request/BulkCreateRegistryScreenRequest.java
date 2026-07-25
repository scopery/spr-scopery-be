package com.company.scopery.modules.traceability.screen.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkCreateRegistryScreenRequest(
        @NotNull @Size(min = 1, max = 100)
        List<@Valid CreateRegistryScreenRequest> items
) {}
