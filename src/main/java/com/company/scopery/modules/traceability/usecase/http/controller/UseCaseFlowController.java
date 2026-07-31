package com.company.scopery.modules.traceability.usecase.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.usecase.application.action.CreateUseCaseFlowAction;
import com.company.scopery.modules.traceability.usecase.application.action.DeleteUseCaseFlowAction;
import com.company.scopery.modules.traceability.usecase.application.action.UpdateUseCaseFlowAction;
import com.company.scopery.modules.traceability.usecase.application.command.CreateUseCaseFlowCommand;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteUseCaseFlowCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseFlowCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowResponse;
import com.company.scopery.modules.traceability.usecase.http.request.CreateUseCaseFlowRequest;
import com.company.scopery.modules.traceability.usecase.http.request.UpdateUseCaseFlowRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Traceability - Use Case Flow")
public class UseCaseFlowController {

    private final CreateUseCaseFlowAction createAction;
    private final UpdateUseCaseFlowAction updateAction;
    private final DeleteUseCaseFlowAction deleteAction;

    public UseCaseFlowController(CreateUseCaseFlowAction createAction,
                                 UpdateUseCaseFlowAction updateAction,
                                 DeleteUseCaseFlowAction deleteAction) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
    }

    @PostMapping(TraceabilityApiPaths.USE_CASE_FLOWS)
    @Operation(summary = "Create a flow for a use case")
    public ApiResponse<UseCaseFlowResponse> create(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @Valid @RequestBody CreateUseCaseFlowRequest r) {
        return ApiResponse.success(createAction.execute(new CreateUseCaseFlowCommand(
                projectId, useCaseId, r.flowType(), r.name(), r.sourceStepId(), r.conditionText())));
    }

    @PutMapping(TraceabilityApiPaths.USE_CASE_FLOW)
    @Operation(summary = "Update a use case flow")
    public ApiResponse<UseCaseFlowResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID flowId,
            @Valid @RequestBody UpdateUseCaseFlowRequest r) {
        return ApiResponse.success(updateAction.execute(new UpdateUseCaseFlowCommand(
                projectId, useCaseId, flowId, r.name(), r.sourceStepId(), r.conditionText())));
    }

    @DeleteMapping(TraceabilityApiPaths.USE_CASE_FLOW)
    @Operation(summary = "Delete a use case flow")
    public ApiResponse<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID flowId) {
        deleteAction.execute(new DeleteUseCaseFlowCommand(projectId, useCaseId, flowId));
        return ApiResponse.success(null);
    }
}
