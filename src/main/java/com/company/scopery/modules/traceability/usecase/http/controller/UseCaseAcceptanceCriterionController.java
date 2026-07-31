package com.company.scopery.modules.traceability.usecase.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.usecase.application.action.AddUseCaseAcceptanceCriterionAction;
import com.company.scopery.modules.traceability.usecase.application.action.DeleteUseCaseAcceptanceCriterionAction;
import com.company.scopery.modules.traceability.usecase.application.action.UpdateUseCaseAcceptanceCriterionAction;
import com.company.scopery.modules.traceability.usecase.application.command.AddUseCaseAcceptanceCriterionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteUseCaseAcceptanceCriterionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseAcceptanceCriterionCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseAcceptanceCriterionResponse;
import com.company.scopery.modules.traceability.usecase.http.request.AddUseCaseAcceptanceCriterionRequest;
import com.company.scopery.modules.traceability.usecase.http.request.UpdateUseCaseAcceptanceCriterionRequest;
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
@Tag(name = "Traceability - Use Case Acceptance Criterion")
public class UseCaseAcceptanceCriterionController {

    private final AddUseCaseAcceptanceCriterionAction addAction;
    private final UpdateUseCaseAcceptanceCriterionAction updateAction;
    private final DeleteUseCaseAcceptanceCriterionAction deleteAction;

    public UseCaseAcceptanceCriterionController(AddUseCaseAcceptanceCriterionAction addAction,
                                                UpdateUseCaseAcceptanceCriterionAction updateAction,
                                                DeleteUseCaseAcceptanceCriterionAction deleteAction) {
        this.addAction = addAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
    }

    @PostMapping(TraceabilityApiPaths.USE_CASE_CRITERIA)
    @Operation(summary = "Add an acceptance criterion to a use case")
    public ApiResponse<UseCaseAcceptanceCriterionResponse> add(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @Valid @RequestBody AddUseCaseAcceptanceCriterionRequest r) {
        return ApiResponse.success(addAction.execute(new AddUseCaseAcceptanceCriterionCommand(
                projectId, useCaseId, r.title(), r.givenText(), r.whenText(), r.thenText(), r.displayOrder())));
    }

    @PutMapping(TraceabilityApiPaths.USE_CASE_CRITERION)
    @Operation(summary = "Update an acceptance criterion")
    public ApiResponse<UseCaseAcceptanceCriterionResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID criterionId,
            @Valid @RequestBody UpdateUseCaseAcceptanceCriterionRequest r) {
        return ApiResponse.success(updateAction.execute(new UpdateUseCaseAcceptanceCriterionCommand(
                projectId, useCaseId, criterionId, r.title(), r.givenText(), r.whenText(), r.thenText(), r.displayOrder())));
    }

    @DeleteMapping(TraceabilityApiPaths.USE_CASE_CRITERION)
    @Operation(summary = "Delete an acceptance criterion")
    public ApiResponse<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID criterionId) {
        deleteAction.execute(new DeleteUseCaseAcceptanceCriterionCommand(projectId, useCaseId, criterionId));
        return ApiResponse.success(null);
    }
}
