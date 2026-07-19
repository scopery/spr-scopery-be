package com.company.scopery.modules.project.templatewbs.application.command;

import java.util.UUID;

public record CreateProjectTemplateWbsNodeCommand(
        UUID templateId,
        UUID versionId,
        UUID parentId,
        UUID templatePhaseId,
        String code,
        String title,
        String description,
        String nodeType,
        int orderIndex,
        UUID deliverableDocumentTypeId
) {}
