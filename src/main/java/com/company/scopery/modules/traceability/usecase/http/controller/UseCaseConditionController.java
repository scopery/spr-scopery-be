package com.company.scopery.modules.traceability.usecase.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.usecase.application.action.AddUseCaseConditionAction;
import com.company.scopery.modules.traceability.usecase.application.action.DeleteUseCaseConditionAction;
import com.company.scopery.modules.traceability.usecase.application.action.UpdateUseCaseConditionAction;
import com.company.scopery.modules.traceability.usecase.application.command.AddUseCaseConditionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteUseCaseConditionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseConditionCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseConditionResponse;
import com.company.scopery.modules.traceability.usecase.http.request.AddUseCaseConditionRequest;
import com.company.scopery.modules.traceability.usecase.http.request.UpdateUseCaseConditionRequest;
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
@Tag(name = "Traceability - Use Case Condition")
public class UseCaseConditionController {

    private final AddUseCaseConditionAction addAction;
    private final UpdateUseCaseConditionAction updateAction;
    private final DeleteUseCaseConditionAction deleteAction;

    public UseCaseConditionController(AddUseCaseConditionAction addAction,
                                      UpdateUseCaseConditionAction updateAction,
                                      DeleteUseCaseConditionAction deleteAction) {
        this.addAction = addAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
    }

    @PostMapping(TraceabilityApiPaths.USE_CASE_CONDITIONS)
    @Operation(summary = "Add a condition to a use case")
    public ApiResponse<UseCaseConditionResponse> add(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @Valid @RequestBody AddUseCaseConditionRequest r) {
        return ApiResponse.success(addAction.execute(new AddUseCaseConditionCommand(
                projectId, useCaseId, r.conditionType(), r.content(), r.displayOrder())));
    }

    @PutMapping(TraceabilityApiPaths.USE_CASE_CONDITION)
    @Operation(summary = "Update a use case condition")
    public ApiResponse<UseCaseConditionResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID conditionId,
            @Valid @RequestBody UpdateUseCaseConditionRequest r) {
        return ApiResponse.success(updateAction.execute(new UpdateUseCaseConditionCommand(
                projectId, useCaseId, conditionId, r.content(), r.displayOrder())));
    }

    @DeleteMapping(TraceabilityApiPaths.USE_CASE_CONDITION)
    @Operation(summary = "Delete a use case condition")
    public ApiResponse<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID conditionId) {
        deleteAction.execute(new DeleteUseCaseConditionCommand(projectId, useCaseId, conditionId));
        return ApiResponse.success(null);
    }
}
