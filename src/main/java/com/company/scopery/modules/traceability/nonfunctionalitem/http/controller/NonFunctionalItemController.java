package com.company.scopery.modules.traceability.nonfunctionalitem.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.nonfunctionalitem.application.action.CreateNonFunctionalItemAction;
import com.company.scopery.modules.traceability.nonfunctionalitem.application.action.DeleteNonFunctionalItemAction;
import com.company.scopery.modules.traceability.nonfunctionalitem.application.action.UpdateNonFunctionalItemAction;
import com.company.scopery.modules.traceability.nonfunctionalitem.application.command.CreateNonFunctionalItemCommand;
import com.company.scopery.modules.traceability.nonfunctionalitem.application.command.UpdateNonFunctionalItemCommand;
import com.company.scopery.modules.traceability.nonfunctionalitem.application.response.NonFunctionalItemResponse;
import com.company.scopery.modules.traceability.nonfunctionalitem.application.service.NonFunctionalItemQueryService;
import com.company.scopery.modules.traceability.nonfunctionalitem.http.request.CreateNonFunctionalItemRequest;
import com.company.scopery.modules.traceability.nonfunctionalitem.http.request.UpdateNonFunctionalItemRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.NON_FUNCTIONAL_ITEMS)
@Tag(name = "Traceability - Non-Functional Items")
public class NonFunctionalItemController {

    private final NonFunctionalItemQueryService queryService;
    private final CreateNonFunctionalItemAction createAction;
    private final UpdateNonFunctionalItemAction updateAction;
    private final DeleteNonFunctionalItemAction deleteAction;

    public NonFunctionalItemController(
            NonFunctionalItemQueryService queryService,
            CreateNonFunctionalItemAction createAction,
            UpdateNonFunctionalItemAction updateAction,
            DeleteNonFunctionalItemAction deleteAction
    ) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
    }

    @PostMapping
    @Operation(summary = "Create a non-functional item")
    public ApiResponse<NonFunctionalItemResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateNonFunctionalItemRequest request
    ) {
        CreateNonFunctionalItemCommand command = new CreateNonFunctionalItemCommand(
                projectId,
                request.workspaceId(),
                request.code(),
                request.title(),
                request.description(),
                request.category(),
                request.priority(),
                request.targetMetric(),
                request.scopeType(),
                request.scopeRefId()
        );
        return ApiResponse.success(createAction.execute(command));
    }

    @GetMapping
    @Operation(summary = "List non-functional items for a project, optionally filtered by scope target")
    public ApiResponse<List<NonFunctionalItemResponse>> list(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID targetId
    ) {
        if (targetId != null) {
            return ApiResponse.success(queryService.listByTarget(projectId, targetId));
        }
        return ApiResponse.success(queryService.listByProject(projectId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a non-functional item by ID")
    public ApiResponse<NonFunctionalItemResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID id
    ) {
        return ApiResponse.success(queryService.get(id, projectId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a non-functional item")
    public ApiResponse<NonFunctionalItemResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateNonFunctionalItemRequest request
    ) {
        UpdateNonFunctionalItemCommand command = new UpdateNonFunctionalItemCommand(
                id,
                projectId,
                request.title(),
                request.description(),
                request.category(),
                request.priority(),
                request.status(),
                request.targetMetric(),
                request.scopeType(),
                request.scopeRefId()
        );
        return ApiResponse.success(updateAction.execute(command));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a non-functional item")
    public ApiResponse<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID id
    ) {
        deleteAction.execute(id, projectId);
        return ApiResponse.success(null);
    }
}
