package com.company.scopery.modules.documenthub.suggestion.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateSuggestionRequest(
        @NotNull long targetRevisionNo,
        String description,
        @NotNull @NotEmpty @Valid List<OperationItem> operations
) {
    public record OperationItem(
            @NotNull String opType,
            String blockId,
            String path,
            String value,
            int ordinal
    ) {}
}
