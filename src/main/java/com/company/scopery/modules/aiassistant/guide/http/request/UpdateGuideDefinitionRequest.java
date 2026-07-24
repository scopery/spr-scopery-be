package com.company.scopery.modules.aiassistant.guide.http.request;

public record UpdateGuideDefinitionRequest(
        String title,
        String bodyMarkdown,
        String status
) {}
