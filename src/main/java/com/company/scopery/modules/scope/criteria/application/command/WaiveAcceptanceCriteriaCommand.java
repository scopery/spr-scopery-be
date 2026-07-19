package com.company.scopery.modules.scope.criteria.application.command;

import java.util.UUID;

public record WaiveAcceptanceCriteriaCommand(
        UUID projectId,
        UUID criteriaId,
        String reason
) {}
