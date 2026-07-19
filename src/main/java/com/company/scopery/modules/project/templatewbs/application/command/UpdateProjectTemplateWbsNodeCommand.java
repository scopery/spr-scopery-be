package com.company.scopery.modules.project.templatewbs.application.command;

import java.util.UUID;

public record UpdateProjectTemplateWbsNodeCommand(
        UUID templateId,
        UUID versionId,
        UUID templateWbsNodeId,
        UUID templatePhaseId,
        String code,
        String title,
        String description,
        String nodeType,
        UUID deliverableDocumentTypeId
) {}
