package com.company.scopery.modules.raid.decision.application.command;

import java.util.UUID;

public record UpdateDecisionOptionCommand(
        UUID projectId,
        UUID decisionId,
        UUID optionId,
        String optionTitle,
        String optionDescription,
        String pros,
        String cons,
        String estimatedImpact
) {}
