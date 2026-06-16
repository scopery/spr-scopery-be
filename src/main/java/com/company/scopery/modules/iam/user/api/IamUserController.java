package com.company.scopery.modules.iam.user.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.company.scopery.modules.iam.user.api.request.CreateIamUserRequest;
import com.company.scopery.modules.iam.user.api.request.UpdateIamUserRequest;
import com.company.scopery.modules.iam.user.application.IamUserApplicationService;
import com.company.scopery.modules.iam.user.application.command.CreateIamUserCommand;
import com.company.scopery.modules.iam.user.application.command.UpdateIamUserCommand;
import com.company.scopery.modules.iam.user.application.query.SearchIamUserQuery;
import com.company.scopery.modules.iam.user.application.response.IamUserResponse;
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

    private final IamUserApplicationService service;

    public IamUserController(IamUserApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new IAM user")
    @PostMapping
    public ResponseEntity<ApiResponse<IamUserResponse>> createUser(
            @Valid @RequestBody CreateIamUserRequest request) {
        CreateIamUserCommand command = new CreateIamUserCommand(
                request.username(), request.email(), request.fullName());
        return ResponseEntity.ok(ApiResponse.success(service.createUser(command)));
    }

    @Operation(summary = "Update IAM user full name")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IamUserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIamUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                service.updateUser(new UpdateIamUserCommand(id, request.fullName()))));
    }

    @Operation(summary = "Get IAM user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamUserResponse>> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getUser(id)));
    }

    @Operation(summary = "Search IAM users")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamUserResponse>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(
                service.searchUsers(new SearchIamUserQuery(keyword, status, page, size)))));
    }

    @Operation(summary = "Activate IAM user")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<IamUserResponse>> activateUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.activateUser(id)));
    }

    @Operation(summary = "Deactivate IAM user")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<IamUserResponse>> deactivateUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.deactivateUser(id)));
    }

    @Operation(summary = "Suspend IAM user")
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<IamUserResponse>> suspendUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.suspendUser(id)));
    }
}
