package com.company.scopery.modules.resourcereference.resourcetype.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateResourceTypeRequest(
        @NotBlank String code,
        @NotBlank String displayName,
        String description
) {}
