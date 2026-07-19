package com.company.scopery.modules.project.templatephase.application.command;

import java.util.List;
import java.util.UUID;

public record ReorderProjectTemplatePhasesCommand(
        UUID templateId,
        UUID versionId,
        List<UUID> orderedPhaseIds
) {}
