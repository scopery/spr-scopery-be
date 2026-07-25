package com.company.scopery.modules.traceability.funcitemprop.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkCreateFunctionalItemCustomPropertyRequest(
        @NotNull @Size(min = 1, max = 50)
        List<@Valid CreateFunctionalItemCustomPropertyRequest> items
) {}
