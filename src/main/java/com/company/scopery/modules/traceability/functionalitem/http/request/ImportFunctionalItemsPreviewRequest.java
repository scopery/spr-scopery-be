package com.company.scopery.modules.traceability.functionalitem.http.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ImportFunctionalItemsPreviewRequest(
        @NotNull @NotEmpty List<ImportFunctionalItemEntry> items
) {}
