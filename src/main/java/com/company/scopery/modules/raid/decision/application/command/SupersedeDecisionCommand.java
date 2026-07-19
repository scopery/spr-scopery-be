package com.company.scopery.modules.raid.decision.application.command;

import java.util.UUID;

public record SupersedeDecisionCommand(
        UUID projectId,
        UUID decisionId,
        UUID replacementDecisionId,
        String title,
        String rationale,
        String category
) {}
