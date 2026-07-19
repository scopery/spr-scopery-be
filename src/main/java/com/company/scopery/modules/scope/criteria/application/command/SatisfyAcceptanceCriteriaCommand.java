package com.company.scopery.modules.scope.criteria.application.command;

import java.util.UUID;

public record SatisfyAcceptanceCriteriaCommand(
        UUID projectId,
        UUID criteriaId
) {}
