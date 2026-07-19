package com.company.scopery.modules.raid.decision.application.command;

import java.util.UUID;

public record CreateDecisionOptionCommand(
        UUID projectId,
        UUID decisionId,
        String optionTitle,
        String optionDescription,
        String pros,
        String cons,
        String estimatedImpact
) {}
