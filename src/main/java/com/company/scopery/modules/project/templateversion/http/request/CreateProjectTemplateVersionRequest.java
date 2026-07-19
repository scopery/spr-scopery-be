package com.company.scopery.modules.project.templateversion.http.request;

import jakarta.validation.constraints.Size;

public record CreateProjectTemplateVersionRequest(
        @Size(max = 255) String name,
        String description
) {}
