package com.company.scopery.modules.traceability.functionalitem.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkCreateFunctionalItemRequest(
        @NotNull @Size(min = 1, max = 100)
        List<@Valid CreateFunctionalItemRequest> items
) {}
