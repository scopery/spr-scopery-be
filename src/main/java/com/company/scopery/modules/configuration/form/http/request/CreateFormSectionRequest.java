package com.company.scopery.modules.configuration.form.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload to add a section to a form version")
public record CreateFormSectionRequest(
        @Schema(description = "Display title of the section", example = "Basic Information")
        @NotBlank String title,

        @Schema(description = "Sort order position of the section within the form", example = "1", nullable = true)
        Integer sortOrder) {}
