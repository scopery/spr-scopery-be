package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.List;
import java.util.UUID;

public record ImportUseCaseNestedCommand(
        UUID projectId,
        UUID useCaseId,
        List<CreateUseCaseCommand.NestedFlow> flows,
        List<CreateUseCaseCommand.NestedCondition> conditions,
        List<CreateUseCaseCommand.NestedBusinessRule> businessRules,
        List<CreateUseCaseCommand.NestedAcceptanceCriterion> acceptanceCriteria,
        List<UUID> supportingFunctionIds
) {}
