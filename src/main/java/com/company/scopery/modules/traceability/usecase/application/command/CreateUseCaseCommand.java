package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.List;
import java.util.UUID;

public record CreateUseCaseCommand(
        UUID projectId,
        UUID primaryFunctionId,
        String key,
        String name,
        String goal,
        String primaryActorName,
        String triggerText,
        List<NestedFlow> flows,
        List<NestedCondition> conditions,
        List<NestedBusinessRule> businessRules,
        List<NestedAcceptanceCriterion> acceptanceCriteria
) {
    /** Shell-only convenience. */
    public CreateUseCaseCommand(
            UUID projectId,
            UUID primaryFunctionId,
            String key,
            String name,
            String goal,
            String primaryActorName,
            String triggerText
    ) {
        this(projectId, primaryFunctionId, key, name, goal, primaryActorName, triggerText,
                null, null, null, null);
    }

    public record NestedFlow(
            String flowType,
            String name,
            String conditionText,
            List<NestedStep> steps
    ) {}

    public record NestedStep(
            String stepType,
            String contentJson,
            Integer displayOrder
    ) {}

    public record NestedCondition(
            String conditionType,
            String content,
            Integer displayOrder
    ) {}

    public record NestedBusinessRule(
            String ruleCode,
            String description,
            Integer displayOrder
    ) {}

    public record NestedAcceptanceCriterion(
            String title,
            String givenText,
            String whenText,
            String thenText,
            Integer displayOrder
    ) {}
}
