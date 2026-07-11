package com.company.scopery.modules.project.wbs.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateWbsNodeRequest(
        @NotBlank String title,
        String description,
        @NotBlank String nodeType
) {}
