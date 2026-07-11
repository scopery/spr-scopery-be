package com.company.scopery.modules.iam.user.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.company.scopery.modules.iam.user.application.action.ActivateIamUserAction;
import com.company.scopery.modules.iam.user.application.action.CreateIamUserAction;
import com.company.scopery.modules.iam.user.application.action.DeactivateIamUserAction;
import com.company.scopery.modules.iam.user.application.action.SuspendIamUserAction;
import com.company.scopery.modules.iam.user.application.action.UpdateIamUserAction;
import com.company.scopery.modules.iam.user.application.command.ActivateIamUserCommand;
import com.company.scopery.modules.iam.user.application.command.CreateIamUserCommand;
import com.company.scopery.modules.iam.user.application.command.DeactivateIamUserCommand;
import com.company.scopery.modules.iam.user.application.command.SuspendIamUserCommand;
import com.company.scopery.modules.iam.user.application.command.UpdateIamUserCommand;
import com.company.scopery.modules.iam.user.application.query.SearchIamUserQuery;
import com.company.scopery.modules.iam.user.application.response.IamUserResponse;
import com.company.scopery.modules.iam.user.application.service.IamUserQueryService;
import com.company.scopery.modules.iam.user.http.request.CreateIamUserRequest;
import com.company.scopery.modules.iam.user.http.request.SearchIamUserRequest;
import com.company.scopery.modules.iam.user.http.request.UpdateIamUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "IAM - Users", description = "Manage IAM user identity references")
@RestController
@RequestMapping(IamApiPaths.IAM_USERS)
public class IamUserController {

    private final CreateIamUserAction createAction;
    private final UpdateIamUserAction updateAction;
    private final ActivateIamUserAction activateAction;
    private final DeactivateIamUserAction deactivateAction;
    private final SuspendIamUserAction suspendAction;
    private final IamUserQueryService queryService;

    public IamUserController(CreateIamUserAction createAction,
                             UpdateIamUserAction updateAction,
                             ActivateIamUserAction activateAction,
                             DeactivateIamUserAction deactivateAction,
                             SuspendIamUserAction suspendAction,
                             IamUserQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.suspendAction = suspendAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new IAM user")
    @PostMapping
    public ResponseEntity<ApiResponse<IamUserResponse>> createUser(
            @Valid @RequestBody CreateIamUserRequest request) {
        CreateIamUserCommand command = new CreateIamUserCommand(
                request.username(), request.email(), request.fullName(), request.password());
        return ResponseEntity.ok(ApiResponse.success(createAction.execute(command)));
    }

    @Operation(summary = "Update IAM user full name")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IamUserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIamUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                updateAction.execute(new UpdateIamUserCommand(id, request.fullName()))));
    }

    @Operation(summary = "Get IAM user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamUserResponse>> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getUser(id)));
    }

    @Operation(summary = "Search IAM users")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamUserResponse>>> searchUsers(
            @ModelAttribute SearchIamUserRequest request) {
        PageResult<IamUserResponse> result = queryService.searchUsers(new SearchIamUserQuery(
                request.keyword(), request.status(), request.page(), request.size()));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }

    @Operation(summary = "Activate IAM user")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<IamUserResponse>> activateUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(new ActivateIamUserCommand(id))));
    }

    @Operation(summary = "Deactivate IAM user")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<IamUserResponse>> deactivateUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(deactivateAction.execute(new DeactivateIamUserCommand(id))));
    }

    @Operation(summary = "Suspend IAM user")
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<IamUserResponse>> suspendUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(suspendAction.execute(new SuspendIamUserCommand(id))));
    }
}
