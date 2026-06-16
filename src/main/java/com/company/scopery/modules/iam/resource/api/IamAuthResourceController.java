package com.company.scopery.modules.iam.resource.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.company.scopery.modules.iam.resource.api.request.CreateIamAuthResourceRequest;
import com.company.scopery.modules.iam.resource.api.request.UpdateIamAuthResourceRequest;
import com.company.scopery.modules.iam.resource.application.IamAuthResourceApplicationService;
import com.company.scopery.modules.iam.resource.application.command.CreateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.command.UpdateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.query.SearchIamAuthResourceQuery;
import com.company.scopery.modules.iam.resource.application.response.IamAuthResourceResponse;
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

    private final IamAuthResourceApplicationService service;

    public IamAuthResourceController(IamAuthResourceApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Register a new auth resource")
    @PostMapping
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> createResource(
            @Valid @RequestBody CreateIamAuthResourceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.createResource(
                new CreateIamAuthResourceCommand(request.code(), request.resourceType(),
                        request.name(), request.description()))));
    }

    @Operation(summary = "Update auth resource name and description")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> updateResource(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIamAuthResourceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.updateResource(
                new UpdateIamAuthResourceCommand(id, request.name(), request.description()))));
    }

    @Operation(summary = "Get auth resource by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> getResource(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getResource(id)));
    }

    @Operation(summary = "Search auth resources")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamAuthResourceResponse>>> searchResources(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(
                service.searchResources(new SearchIamAuthResourceQuery(
                        keyword, resourceType, status, page, size)))));
    }

    @Operation(summary = "Activate auth resource")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> activateResource(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.activateResource(id)));
    }

    @Operation(summary = "Deactivate auth resource")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<IamAuthResourceResponse>> deactivateResource(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.deactivateResource(id)));
    }
}
