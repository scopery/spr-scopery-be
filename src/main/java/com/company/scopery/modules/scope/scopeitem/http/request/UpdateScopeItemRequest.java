package com.company.scopery.modules.scope.scopeitem.http.request;

public record UpdateScopeItemRequest(
        String title,
        String description,
        Boolean inScope,
        Boolean outOfScope,
        String priority,
        Boolean acceptanceRequired,
        Integer sortOrder
) {}
