package com.company.scopery.modules.iam.ownerpolicy.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.ownerpolicy.application.action.CreateIamOwnerPolicyAction;
import com.company.scopery.modules.iam.ownerpolicy.application.command.CreateIamOwnerPolicyCommand;
import com.company.scopery.modules.iam.ownerpolicy.application.response.IamOwnerPolicyResponse;
import com.company.scopery.modules.iam.ownerpolicy.application.service.IamOwnerPolicyQueryService;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;
import com.company.scopery.modules.iam.ownerpolicy.http.request.CreateIamOwnerPolicyRequest;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(IamApiPaths.IAM_OWNER_POLICIES)
@Tag(name = "IAM - Owner Policies")
public class IamOwnerPolicyController {
    private final CreateIamOwnerPolicyAction createAction;
    private final IamOwnerPolicyQueryService queryService;

    public IamOwnerPolicyController(CreateIamOwnerPolicyAction createAction,
                                    IamOwnerPolicyQueryService queryService) {
        this.createAction = createAction;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Create and activate a new owner-policy version")
    public ResponseEntity<ApiResponse<IamOwnerPolicyResponse>> create(
            @Valid @RequestBody CreateIamOwnerPolicyRequest request) {
        var actions = request.actions().stream()
                .map(action -> new IamOwnerPolicyAction(action.permissionCode(), action.actionCode())).toList();
        var command = new CreateIamOwnerPolicyCommand(request.resourceType(), request.inheritanceScope(),
                request.canDelegate(), request.delegationDepth(), actions);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createAction.execute(command)));
    }

    @GetMapping
    @Operation(summary = "List owner-policy versions")
    public ApiResponse<List<IamOwnerPolicyResponse>> list() {
        return ApiResponse.success(queryService.listAll());
    }
}
