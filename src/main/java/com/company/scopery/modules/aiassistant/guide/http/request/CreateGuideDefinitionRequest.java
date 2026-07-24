package com.company.scopery.modules.aiassistant.guide.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateGuideDefinitionRequest(
        @NotBlank String pageCode,
        @NotBlank String locale,
        @NotBlank String title,
        @NotBlank String bodyMarkdown,
        String fieldCode,
        String actionCode
) {}
