package com.company.scopery.modules.iam.grant.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.grant.api.request.AddIamGrantRightRequest;
import com.company.scopery.modules.iam.grant.api.request.CreateIamAccessGrantRequest;
import com.company.scopery.modules.iam.grant.application.IamAccessGrantApplicationService;
import com.company.scopery.modules.iam.grant.application.command.AddIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.command.CreateIamAccessGrantCommand;
import com.company.scopery.modules.iam.grant.application.command.RemoveIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.query.SearchIamAccessGrantQuery;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantResponse;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantRightResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "IAM - Access Grants", description = "Manage access grants and their rights")
@RestController
@RequestMapping(IamApiPaths.IAM_ACCESS_GRANTS)
public class IamAccessGrantController {

    private final IamAccessGrantApplicationService service;

    public IamAccessGrantController(IamAccessGrantApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Create an access grant")
    @PostMapping
    public ResponseEntity<ApiResponse<IamAccessGrantResponse>> createGrant(
            @Valid @RequestBody CreateIamAccessGrantRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.createGrant(
                new CreateIamAccessGrantCommand(request.subjectType(), request.subjectId(),
                        request.resourceId(), request.roleId(),
                        request.effect(), request.scopeType(), request.scopeRefId(),
                        request.workspaceId(), request.grantedBy()))));
    }

    @Operation(summary = "Get access grant by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamAccessGrantResponse>> getGrant(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getGrant(id)));
    }

    @Operation(summary = "Search access grants")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamAccessGrantResponse>>> searchGrants(
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(required = false) UUID resourceId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(
                service.searchGrants(new SearchIamAccessGrantQuery(subjectId, resourceId, status, page, size)))));
    }

    @Operation(summary = "Revoke an access grant")
    @PatchMapping("/{id}/revoke")
    public ResponseEntity<ApiResponse<IamAccessGrantResponse>> revokeGrant(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.revokeGrant(id)));
    }

    @Operation(summary = "List rights attached to a grant")
    @GetMapping("/{id}/rights")
    public ResponseEntity<ApiResponse<List<IamAccessGrantRightResponse>>> getGrantRights(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getGrantRights(id)));
    }

    @Operation(summary = "Add a right to a grant")
    @PostMapping("/{id}/rights")
    public ResponseEntity<ApiResponse<IamAccessGrantRightResponse>> addRight(
            @PathVariable UUID id,
            @Valid @RequestBody AddIamGrantRightRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.addRight(
                new AddIamGrantRightCommand(id, request.rightId()))));
    }

    @Operation(summary = "Remove a right from a grant")
    @DeleteMapping("/{id}/rights/{rightId}")
    public ResponseEntity<ApiResponse<Void>> removeRight(@PathVariable UUID id, @PathVariable UUID rightId) {
        service.removeRight(new RemoveIamGrantRightCommand(id, rightId));
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
