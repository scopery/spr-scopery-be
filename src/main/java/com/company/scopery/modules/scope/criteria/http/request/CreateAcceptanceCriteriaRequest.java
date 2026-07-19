package com.company.scopery.modules.scope.criteria.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAcceptanceCriteriaRequest(
        @NotBlank String title,
        String type,
        String description,
        Boolean mandatory
) {}
