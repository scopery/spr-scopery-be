package com.company.scopery.modules.scope.scopeitem.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateScopeItemRequest(
        @NotBlank String type,
        String code,
        @NotBlank String title,
        String description,
        Boolean inScope,
        Boolean outOfScope,
        String priority,
        Boolean acceptanceRequired,
        Integer sortOrder
) {}
