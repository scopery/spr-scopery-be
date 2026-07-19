package com.company.scopery.modules.project.template.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectTemplateRequest(
        @NotBlank @Size(max = 255) String name,
        String description,
        String category,
        String visibility
) {}
