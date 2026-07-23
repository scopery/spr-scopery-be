package com.company.scopery.modules.traceability.businessrule.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.businessrule.application.action.CreateBusinessRuleAction;
import com.company.scopery.modules.traceability.businessrule.application.action.DeleteBusinessRuleAction;
import com.company.scopery.modules.traceability.businessrule.application.action.UpdateBusinessRuleAction;
import com.company.scopery.modules.traceability.businessrule.application.command.CreateBusinessRuleCommand;
import com.company.scopery.modules.traceability.businessrule.application.command.UpdateBusinessRuleCommand;
import com.company.scopery.modules.traceability.businessrule.application.response.BusinessRuleResponse;
import com.company.scopery.modules.traceability.businessrule.application.service.BusinessRuleQueryService;
import com.company.scopery.modules.traceability.businessrule.http.request.CreateBusinessRuleRequest;
import com.company.scopery.modules.traceability.businessrule.http.request.UpdateBusinessRuleRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.FUNCTIONAL_ITEM_BUSINESS_RULES)
@Tag(name = "Traceability - Business Rules")
public class BusinessRuleController {

    private final CreateBusinessRuleAction create;
    private final UpdateBusinessRuleAction update;
    private final DeleteBusinessRuleAction delete;
    private final BusinessRuleQueryService query;

    public BusinessRuleController(
            CreateBusinessRuleAction create,
            UpdateBusinessRuleAction update,
            DeleteBusinessRuleAction delete,
            BusinessRuleQueryService query
    ) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create a business rule for a functional item")
    public ApiResponse<BusinessRuleResponse> create(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @Valid @RequestBody CreateBusinessRuleRequest r
    ) {
        return ApiResponse.success(create.execute(new CreateBusinessRuleCommand(
                functionalItemId, projectId, r.code(), r.title(), r.description(), r.severity())));
    }

    @GetMapping
    @Operation(summary = "List business rules for a functional item")
    public ApiResponse<List<BusinessRuleResponse>> list(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId
    ) {
        return ApiResponse.success(query.listByFunctionalItem(functionalItemId, projectId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a business rule by ID")
    public ApiResponse<BusinessRuleResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID id
    ) {
        return ApiResponse.success(query.get(id, functionalItemId, projectId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a business rule")
    public ApiResponse<BusinessRuleResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBusinessRuleRequest r
    ) {
        return ApiResponse.success(update.execute(new UpdateBusinessRuleCommand(
                id, functionalItemId, projectId, r.title(), r.description(), r.severity(), r.status())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a business rule")
    public ApiResponse<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID id
    ) {
        delete.execute(id, functionalItemId, projectId);
        return ApiResponse.success(null);
    }
}
