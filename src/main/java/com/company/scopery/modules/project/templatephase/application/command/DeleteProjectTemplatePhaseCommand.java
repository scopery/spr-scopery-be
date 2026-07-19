package com.company.scopery.modules.project.templatephase.application.command;

import java.util.UUID;

public record DeleteProjectTemplatePhaseCommand(
        UUID templateId,
        UUID versionId,
        UUID templatePhaseId,
        boolean cascade
) {}
