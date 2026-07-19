package com.company.scopery.modules.raid.decision.application.command;

import java.util.UUID;

public record CreateDecisionCommand(
        UUID projectId,
        String title,
        String rationale,
        String category,
        String code
) {}
