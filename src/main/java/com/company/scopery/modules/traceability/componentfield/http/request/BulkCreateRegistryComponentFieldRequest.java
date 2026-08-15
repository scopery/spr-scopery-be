package com.company.scopery.modules.traceability.componentfield.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkCreateRegistryComponentFieldRequest(
        @NotNull @Size(min = 1, max = 500)
        List<@Valid CreateRegistryComponentFieldRequest> items
) {}
