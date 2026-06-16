package com.company.scopery.modules.iam.resource.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateIamAuthResourceRequest(
        @NotBlank @Size(min = 2, max = 100) String code,
        @NotBlank String resourceType,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description) {}
