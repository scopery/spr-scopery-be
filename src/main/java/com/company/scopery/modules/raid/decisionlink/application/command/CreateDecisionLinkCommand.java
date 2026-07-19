package com.company.scopery.modules.raid.decisionlink.application.command;

import java.util.UUID;

public record CreateDecisionLinkCommand(
        UUID projectId,
        UUID decisionId,
        String linkType,
        String targetType,
        UUID targetId
) {}
