package com.company.scopery.modules.iam.grant.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.grant.application.action.AddIamGrantPermissionActionAction;
import com.company.scopery.modules.iam.grant.application.action.AddIamGrantRightAction;
import com.company.scopery.modules.iam.grant.application.action.CreateIamAccessGrantAction;
import com.company.scopery.modules.iam.grant.application.action.DelegateIamAccessAction;
import com.company.scopery.modules.iam.grant.application.action.RemoveIamGrantPermissionActionAction;
import com.company.scopery.modules.iam.grant.application.action.RemoveIamGrantRightAction;
import com.company.scopery.modules.iam.grant.application.action.RevokeIamAccessGrantAction;
import com.company.scopery.modules.iam.grant.application.command.AddIamGrantPermissionActionCommand;
import com.company.scopery.modules.iam.grant.application.command.AddIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.command.CreateIamAccessGrantCommand;
import com.company.scopery.modules.iam.grant.application.command.DelegateIamAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.RemoveIamGrantPermissionActionCommand;
import com.company.scopery.modules.iam.grant.application.command.RemoveIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.command.RevokeIamAccessGrantCommand;
import com.company.scopery.modules.iam.grant.application.query.SearchIamAccessGrantQuery;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantPermissionActionResponse;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantResponse;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantRightResponse;
import com.company.scopery.modules.iam.grant.application.service.IamAccessGrantQueryService;
import com.company.scopery.modules.iam.grant.http.request.AddIamGrantPermissionActionRequest;
import com.company.scopery.modules.iam.grant.http.request.AddIamGrantRightRequest;
import com.company.scopery.modules.iam.grant.http.request.CreateIamAccessGrantRequest;
import com.company.scopery.modules.iam.grant.http.request.DelegateIamAccessRequest;
import com.company.scopery.modules.iam.grant.http.request.RevokeIamGrantRequest;
import com.company.scopery.modules.iam.grant.http.request.SearchIamAccessGrantRequest;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final CreateIamAccessGrantAction createAction;
    private final DelegateIamAccessAction delegateAction;
    private final RevokeIamAccessGrantAction revokeAction;
    private final AddIamGrantRightAction addRightAction;
    private final RemoveIamGrantRightAction removeRightAction;
    private final AddIamGrantPermissionActionAction addPermActionAction;
    private final RemoveIamGrantPermissionActionAction removePermActionAction;
    private final IamAccessGrantQueryService queryService;
    private final ObjectMapper objectMapper;

    public IamAccessGrantController(CreateIamAccessGrantAction createAction,
                                    DelegateIamAccessAction delegateAction,
                                    RevokeIamAccessGrantAction revokeAction,
                                    AddIamGrantRightAction addRightAction,
                                    RemoveIamGrantRightAction removeRightAction,
                                    AddIamGrantPermissionActionAction addPermActionAction,
                                    RemoveIamGrantPermissionActionAction removePermActionAction,
                                    IamAccessGrantQueryService queryService,
                                    ObjectMapper objectMapper) {
        this.createAction = createAction;
        this.delegateAction = delegateAction;
        this.revokeAction = revokeAction;
        this.addRightAction = addRightAction;
        this.removeRightAction = removeRightAction;
        this.addPermActionAction = addPermActionAction;
        this.removePermActionAction = removePermActionAction;
        this.queryService = queryService;
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Delegate permission actions without broadening authority")
    @PostMapping(":delegate")
    public ApiResponse<IamAccessGrantResponse> delegate(@Valid @RequestBody DelegateIamAccessRequest request)
            throws JsonProcessingException {
        var actions = request.actions().stream()
                .map(action -> new IamOwnerPolicyAction(action.permissionCode(), action.actionCode())).toList();
        String conditionJson = request.condition() == null ? null : objectMapper.writeValueAsString(request.condition());
        var command = new DelegateIamAccessCommand(request.subjectType(), request.subjectId(),
                request.resourceType(), request.resourceRefId(), request.delegationDepth(),
                request.expiresAt(), conditionJson, request.reason(), actions);
        return ApiResponse.success(delegateAction.execute(command));
    }

    @Operation(summary = "Revoke an access grant by grant ID in request body")
    @PostMapping(":revoke")
    public ApiResponse<IamAccessGrantResponse> revokeByBody(@Valid @RequestBody RevokeIamGrantRequest request) {
        return ApiResponse.success(revokeAction.execute(new RevokeIamAccessGrantCommand(request.grantId())));
    }

    @Operation(summary = "Create an access grant")
    @PostMapping
    public ResponseEntity<ApiResponse<IamAccessGrantResponse>> createGrant(
            @Valid @RequestBody CreateIamAccessGrantRequest request) {
        return ResponseEntity.ok(ApiResponse.success(createAction.execute(
                new CreateIamAccessGrantCommand(request.subjectType(), request.subjectId(),
                        request.resourceId(), request.roleId(),
                        request.effect(), request.scopeType(), request.scopeRefId(),
                        request.workspaceId(), request.expiresAt()))));
    }

    @Operation(summary = "Get access grant by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamAccessGrantResponse>> getGrant(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getGrant(id)));
    }

    @Operation(summary = "Search access grants")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamAccessGrantResponse>>> searchGrants(
            @ModelAttribute SearchIamAccessGrantRequest request) {
        PageResult<IamAccessGrantResponse> result = queryService.searchGrants(new SearchIamAccessGrantQuery(
                request.subjectId(), request.resourceId(), request.workspaceId(),
                request.status(), request.page(), request.size()));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Revoke an access grant")
    @PatchMapping("/{id}/revoke")
    public ResponseEntity<ApiResponse<IamAccessGrantResponse>> revokeGrant(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(revokeAction.execute(new RevokeIamAccessGrantCommand(id))));
    }

    @Operation(summary = "List rights attached to a grant")
    @GetMapping("/{id}/rights")
    public ResponseEntity<ApiResponse<List<IamAccessGrantRightResponse>>> getGrantRights(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getGrantRights(id)));
    }

    @Operation(summary = "Add a right to a grant")
    @PostMapping("/{id}/rights")
    public ResponseEntity<ApiResponse<IamAccessGrantRightResponse>> addRight(
            @PathVariable UUID id,
            @Valid @RequestBody AddIamGrantRightRequest request) {
        return ResponseEntity.ok(ApiResponse.success(addRightAction.execute(
                new AddIamGrantRightCommand(id, request.rightId()))));
    }

    @Operation(summary = "Remove a right from a grant")
    @DeleteMapping("/{id}/rights/{rightId}")
    public ResponseEntity<ApiResponse<Void>> removeRight(@PathVariable UUID id, @PathVariable UUID rightId) {
        removeRightAction.execute(new RemoveIamGrantRightCommand(id, rightId));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "List permission actions attached to a grant")
    @GetMapping("/{id}/actions")
    public ResponseEntity<ApiResponse<List<IamAccessGrantPermissionActionResponse>>> getGrantActions(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getGrantActions(id)));
    }

    @Operation(summary = "Add a permission action to a grant")
    @PostMapping("/{id}/actions")
    public ResponseEntity<ApiResponse<IamAccessGrantPermissionActionResponse>> addAction(
            @PathVariable UUID id,
            @Valid @RequestBody AddIamGrantPermissionActionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(addPermActionAction.execute(
                new AddIamGrantPermissionActionCommand(id, request.permissionActionId(),
                        request.permissionCode(), request.actionCode()))));
    }

    @Operation(summary = "Remove a permission action from a grant")
    @DeleteMapping("/{id}/actions/{permissionActionId}")
    public ResponseEntity<ApiResponse<Void>> removeAction(@PathVariable UUID id,
                                                          @PathVariable UUID permissionActionId) {
        removePermActionAction.execute(new RemoveIamGrantPermissionActionCommand(id, permissionActionId));
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
