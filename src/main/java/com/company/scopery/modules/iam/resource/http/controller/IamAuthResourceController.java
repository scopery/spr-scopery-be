package com.company.scopery.modules.iam.resource.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.company.scopery.modules.iam.resource.application.action.ActivateIamAuthResourceAction;
import com.company.scopery.modules.iam.resource.application.action.CreateIamAuthResourceAction;
import com.company.scopery.modules.iam.resource.application.action.DeactivateIamAuthResourceAction;
import com.company.scopery.modules.iam.resource.application.action.UpdateIamAuthResourceAction;
import com.company.scopery.modules.iam.resource.application.command.ActivateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.command.CreateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.command.DeactivateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.command.UpdateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.query.SearchIamAuthResourceQuery;
import com.company.scopery.modules.iam.resource.application.response.IamAuthResourceResponse;
import com.company.scopery.modules.iam.resource.application.service.IamAuthResourceQueryService;
import com.company.scopery.modules.iam.resource.http.request.CreateIamAuthResourceRequest;
import com.company.scopery.modules.iam.resource.http.request.SearchIamAuthResourceRequest;
import com.company.scopery.modules.iam.resource.http.request.UpdateIamAuthResourceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "IAM - Auth Resources", description = "Manage protected auth resources")
@RestController
@RequestMapping(IamApiPaths.IAM_AUTH_RESOURCES)
public class IamAuthResourceController {

    private final CreateIamAuthResourceAction createAction;
    private final UpdateIamAuthResourceAction updateAction;
    private final ActivateIamAuthResourceAction activateAction;
    private final DeactivateIamAuthResourceAction deactivateAction;
    private final IamAuthResourceQueryService queryService;

    public IamAuthResourceController(CreateIamAuthResourceAction createAction,
                                     UpdateIamAuthResourceAction updateAction,
                                     ActivateIamAuthResourceAction activateAction,
                                     DeactivateIamAuthResourceAction deactivateAction,
                                     IamAuthResourceQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Register a new auth resource")
    @PostMapping
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> createResource(
            @Valid @RequestBody CreateIamAuthResourceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(createAction.execute(
                new CreateIamAuthResourceCommand(request.code(), request.resourceType(),
                        request.name(), request.description()))));
    }

    @Operation(summary = "Update auth resource name and description")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> updateResource(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIamAuthResourceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(updateAction.execute(
                new UpdateIamAuthResourceCommand(id, request.name(), request.description()))));
    }

    @Operation(summary = "Get auth resource by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> getResource(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getResource(id)));
    }

    @Operation(summary = "Search auth resources")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamAuthResourceResponse>>> searchResources(
            @ModelAttribute SearchIamAuthResourceRequest request) {
        PageResult<IamAuthResourceResponse> result = queryService.searchResources(new SearchIamAuthResourceQuery(
                request.keyword(), request.resourceType(), request.status(),
                request.page(), request.size()));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Get auth resource by business reference")
    @GetMapping("/by-ref")
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> getResourceByRef(
            @RequestParam String resourceType,
            @RequestParam UUID refId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getResourceByRef(resourceType, refId)));
    }

    @Operation(summary = "Get auth resource by code and type")
    @GetMapping("/by-code")
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> getResourceByCode(
            @RequestParam String resourceType,
            @RequestParam String code) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getResourceByCode(resourceType, code)));
    }

    @Operation(summary = "Activate auth resource")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> activateResource(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(new ActivateIamAuthResourceCommand(id))));
    }

    @Operation(summary = "Deactivate auth resource")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> deactivateResource(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(deactivateAction.execute(new DeactivateIamAuthResourceCommand(id))));
    }
}
