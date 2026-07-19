package com.company.scopery.modules.project.templatephase.application.command;

import java.util.UUID;

public record CreateProjectTemplatePhaseCommand(
        UUID templateId,
        UUID versionId,
        UUID phaseDefinitionId,
        String code,
        String name,
        String description,
        int displayOrder,
        Integer defaultDurationDays,
        Integer startOffsetDays,
        UUID deliverableDocumentTypeId
) {}
