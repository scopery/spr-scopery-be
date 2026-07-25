package com.company.scopery.modules.traceability.dataentity.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkCreateRegistryDataEntityRequest(
        @NotNull @Size(min = 1, max = 100)
        List<@Valid CreateRegistryDataEntityRequest> items
) {}
