package com.company.scopery.modules.traceability.usecase.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.usecase.application.action.AddUseCaseBusinessRuleAction;
import com.company.scopery.modules.traceability.usecase.application.action.DeleteUseCaseBusinessRuleAction;
import com.company.scopery.modules.traceability.usecase.application.action.UpdateUseCaseBusinessRuleAction;
import com.company.scopery.modules.traceability.usecase.application.command.AddUseCaseBusinessRuleCommand;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteUseCaseBusinessRuleCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseBusinessRuleCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseBusinessRuleResponse;
import com.company.scopery.modules.traceability.usecase.http.request.AddUseCaseBusinessRuleRequest;
import com.company.scopery.modules.traceability.usecase.http.request.UpdateUseCaseBusinessRuleRequest;
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
@Tag(name = "Traceability - Use Case Business Rule")
public class UseCaseBusinessRuleController {

    private final AddUseCaseBusinessRuleAction addAction;
    private final UpdateUseCaseBusinessRuleAction updateAction;
    private final DeleteUseCaseBusinessRuleAction deleteAction;

    public UseCaseBusinessRuleController(AddUseCaseBusinessRuleAction addAction,
                                         UpdateUseCaseBusinessRuleAction updateAction,
                                         DeleteUseCaseBusinessRuleAction deleteAction) {
        this.addAction = addAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
    }

    @PostMapping(TraceabilityApiPaths.USE_CASE_BUSINESS_RULES)
    @Operation(summary = "Add a business rule to a use case")
    public ApiResponse<UseCaseBusinessRuleResponse> add(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @Valid @RequestBody AddUseCaseBusinessRuleRequest r) {
        return ApiResponse.success(addAction.execute(new AddUseCaseBusinessRuleCommand(
                projectId, useCaseId, r.ruleCode(), r.description(), r.displayOrder())));
    }

    @PutMapping(TraceabilityApiPaths.USE_CASE_BUSINESS_RULE)
    @Operation(summary = "Update a use case business rule")
    public ApiResponse<UseCaseBusinessRuleResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID ruleId,
            @Valid @RequestBody UpdateUseCaseBusinessRuleRequest r) {
        return ApiResponse.success(updateAction.execute(new UpdateUseCaseBusinessRuleCommand(
                projectId, useCaseId, ruleId, r.ruleCode(), r.description(), r.displayOrder())));
    }

    @DeleteMapping(TraceabilityApiPaths.USE_CASE_BUSINESS_RULE)
    @Operation(summary = "Delete a use case business rule")
    public ApiResponse<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID ruleId) {
        deleteAction.execute(new DeleteUseCaseBusinessRuleCommand(projectId, useCaseId, ruleId));
        return ApiResponse.success(null);
    }
}
