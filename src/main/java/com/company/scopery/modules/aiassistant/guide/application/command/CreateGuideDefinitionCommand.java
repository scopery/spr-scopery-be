package com.company.scopery.modules.aiassistant.guide.application.command;

public record CreateGuideDefinitionCommand(
        String pageCode,
        String locale,
        String title,
        String bodyMarkdown,
        String fieldCode,
        String actionCode
) {}
