package com.company.scopery.modules.traceability.usecase.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.usecase.application.action.AddFlowStepAction;
import com.company.scopery.modules.traceability.usecase.application.action.DeleteFlowStepAction;
import com.company.scopery.modules.traceability.usecase.application.action.ReorderFlowStepsAction;
import com.company.scopery.modules.traceability.usecase.application.action.UpdateFlowStepAction;
import com.company.scopery.modules.traceability.usecase.application.command.AddFlowStepCommand;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteFlowStepCommand;
import com.company.scopery.modules.traceability.usecase.application.command.ReorderFlowStepsCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateFlowStepCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowStepResponse;
import com.company.scopery.modules.traceability.usecase.http.request.AddFlowStepRequest;
import com.company.scopery.modules.traceability.usecase.http.request.ReorderFlowStepsRequest;
import com.company.scopery.modules.traceability.usecase.http.request.UpdateFlowStepRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Traceability - Use Case Flow Step")
public class UseCaseFlowStepController {

    private final AddFlowStepAction addAction;
    private final UpdateFlowStepAction updateAction;
    private final DeleteFlowStepAction deleteAction;
    private final ReorderFlowStepsAction reorderAction;

    public UseCaseFlowStepController(AddFlowStepAction addAction,
                                     UpdateFlowStepAction updateAction,
                                     DeleteFlowStepAction deleteAction,
                                     ReorderFlowStepsAction reorderAction) {
        this.addAction = addAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
        this.reorderAction = reorderAction;
    }

    @PostMapping(TraceabilityApiPaths.USE_CASE_FLOW_STEPS)
    @Operation(summary = "Add a step to a use case flow")
    public ApiResponse<UseCaseFlowStepResponse> add(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID flowId,
            @Valid @RequestBody AddFlowStepRequest r) {
        return ApiResponse.success(addAction.execute(new AddFlowStepCommand(
                projectId, useCaseId, flowId,
                r.stepType(), r.screenContextId(), r.contentJson(), r.nextScreenId(), r.displayOrder())));
    }

    @PutMapping(TraceabilityApiPaths.USE_CASE_FLOW_STEP)
    @Operation(summary = "Update a flow step")
    public ApiResponse<UseCaseFlowStepResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID flowId,
            @PathVariable UUID stepId,
            @Valid @RequestBody UpdateFlowStepRequest r) {
        return ApiResponse.success(updateAction.execute(new UpdateFlowStepCommand(
                projectId, useCaseId, flowId, stepId,
                r.stepType(), r.screenContextId(), r.contentJson(), r.nextScreenId())));
    }

    @DeleteMapping(TraceabilityApiPaths.USE_CASE_FLOW_STEP)
    @Operation(summary = "Delete a flow step")
    public ApiResponse<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID flowId,
            @PathVariable UUID stepId) {
        deleteAction.execute(new DeleteFlowStepCommand(projectId, useCaseId, flowId, stepId));
        return ApiResponse.success(null);
    }

    @PutMapping(TraceabilityApiPaths.USE_CASE_FLOW_STEPS_ORDER)
    @Operation(summary = "Reorder steps in a flow")
    public ApiResponse<List<UseCaseFlowStepResponse>> reorder(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID flowId,
            @Valid @RequestBody ReorderFlowStepsRequest r) {
        return ApiResponse.success(reorderAction.execute(
                new ReorderFlowStepsCommand(projectId, useCaseId, flowId, r.stepIds())));
    }
}
