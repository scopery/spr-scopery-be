package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.usecase.application.command.AddFlowStepCommand;
import com.company.scopery.modules.traceability.usecase.application.command.AddSupportingFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.AddUseCaseAcceptanceCriterionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.AddUseCaseBusinessRuleCommand;
import com.company.scopery.modules.traceability.usecase.application.command.AddUseCaseConditionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.CreateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.CreateUseCaseFlowCommand;
import com.company.scopery.modules.traceability.usecase.application.command.ImportUseCaseNestedCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Applies nested use-case parts in-process (no extra HTTP). Used by create/bulk and nested-import.
 */
@Component
public class ApplyUseCaseNestedPartsAction {

    private final CreateUseCaseFlowAction createFlowAction;
    private final AddFlowStepAction addFlowStepAction;
    private final AddUseCaseConditionAction addConditionAction;
    private final AddUseCaseBusinessRuleAction addBusinessRuleAction;
    private final AddUseCaseAcceptanceCriterionAction addAcceptanceCriterionAction;
    private final AddSupportingFunctionAction addSupportingFunctionAction;

    public ApplyUseCaseNestedPartsAction(
            CreateUseCaseFlowAction createFlowAction,
            AddFlowStepAction addFlowStepAction,
            AddUseCaseConditionAction addConditionAction,
            AddUseCaseBusinessRuleAction addBusinessRuleAction,
            AddUseCaseAcceptanceCriterionAction addAcceptanceCriterionAction,
            AddSupportingFunctionAction addSupportingFunctionAction) {
        this.createFlowAction = createFlowAction;
        this.addFlowStepAction = addFlowStepAction;
        this.addConditionAction = addConditionAction;
        this.addBusinessRuleAction = addBusinessRuleAction;
        this.addAcceptanceCriterionAction = addAcceptanceCriterionAction;
        this.addSupportingFunctionAction = addSupportingFunctionAction;
    }

    @Transactional
    public int applyFromCreateCommand(CreateUseCaseCommand c, UUID useCaseId) {
        return apply(
                c.projectId(),
                useCaseId,
                c.flows(),
                c.conditions(),
                c.businessRules(),
                c.acceptanceCriteria(),
                null
        );
    }

    @Transactional
    public int apply(ImportUseCaseNestedCommand c) {
        return apply(
                c.projectId(),
                c.useCaseId(),
                c.flows(),
                c.conditions(),
                c.businessRules(),
                c.acceptanceCriteria(),
                c.supportingFunctionIds()
        );
    }

    private int apply(
            UUID projectId,
            UUID useCaseId,
            List<CreateUseCaseCommand.NestedFlow> flows,
            List<CreateUseCaseCommand.NestedCondition> conditions,
            List<CreateUseCaseCommand.NestedBusinessRule> businessRules,
            List<CreateUseCaseCommand.NestedAcceptanceCriterion> acceptanceCriteria,
            List<UUID> supportingFunctionIds
    ) {
        int created = 0;

        if (flows != null) {
            for (CreateUseCaseCommand.NestedFlow flow : flows) {
                UseCaseFlowResponse createdFlow = createFlowAction.execute(new CreateUseCaseFlowCommand(
                        projectId, useCaseId, flow.flowType(), flow.name(), null, flow.conditionText()));
                created += 1;
                if (flow.steps() == null || flow.steps().isEmpty()) continue;
                int order = 0;
                for (CreateUseCaseCommand.NestedStep step : flow.steps()) {
                    int displayOrder = step.displayOrder() != null ? step.displayOrder() : order;
                    addFlowStepAction.execute(new AddFlowStepCommand(
                            projectId,
                            useCaseId,
                            createdFlow.id(),
                            step.stepType(),
                            null,
                            step.contentJson(),
                            null,
                            displayOrder
                    ));
                    created += 1;
                    order += 1;
                }
            }
        }

        if (conditions != null) {
            int order = 0;
            for (CreateUseCaseCommand.NestedCondition condition : conditions) {
                int displayOrder = condition.displayOrder() != null ? condition.displayOrder() : order;
                addConditionAction.execute(new AddUseCaseConditionCommand(
                        projectId, useCaseId, condition.conditionType(), condition.content(), displayOrder));
                created += 1;
                order += 1;
            }
        }

        if (businessRules != null) {
            int order = 0;
            for (CreateUseCaseCommand.NestedBusinessRule rule : businessRules) {
                int displayOrder = rule.displayOrder() != null ? rule.displayOrder() : order;
                addBusinessRuleAction.execute(new AddUseCaseBusinessRuleCommand(
                        projectId, useCaseId, rule.ruleCode(), rule.description(), displayOrder));
                created += 1;
                order += 1;
            }
        }

        if (acceptanceCriteria != null) {
            int order = 0;
            for (CreateUseCaseCommand.NestedAcceptanceCriterion criterion : acceptanceCriteria) {
                int displayOrder = criterion.displayOrder() != null ? criterion.displayOrder() : order;
                addAcceptanceCriterionAction.execute(new AddUseCaseAcceptanceCriterionCommand(
                        projectId, useCaseId,
                        criterion.title(), criterion.givenText(), criterion.whenText(), criterion.thenText(),
                        displayOrder));
                created += 1;
                order += 1;
            }
        }

        if (supportingFunctionIds != null) {
            for (UUID functionId : supportingFunctionIds) {
                addSupportingFunctionAction.execute(new AddSupportingFunctionCommand(
                        projectId, useCaseId, functionId));
                created += 1;
            }
        }

        return created;
    }
}
