package com.company.scopery.modules.iam.resource.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateIamAuthResourceRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description) {}
