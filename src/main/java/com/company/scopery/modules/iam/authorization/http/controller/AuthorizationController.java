package com.company.scopery.modules.iam.authorization.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.authorization.application.query.CheckAccessByRightQuery;
import com.company.scopery.modules.iam.authorization.application.query.CheckAuthorizationQuery;
import com.company.scopery.modules.iam.authorization.application.response.AuthorizationDecisionResponse;
import com.company.scopery.modules.iam.authorization.application.response.AuthorizationExplanationResponse;
import com.company.scopery.modules.iam.authorization.application.service.AuthorizationQueryService;
import com.company.scopery.modules.iam.authorization.http.request.CheckAccessRequest;
import com.company.scopery.modules.iam.authorization.http.request.CheckAuthorizationBatchRequest;
import com.company.scopery.modules.iam.authorization.http.request.CheckAuthorizationRequest;
import com.company.scopery.modules.iam.authorization.http.request.ExplainAuthorizationRequest;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(IamApiPaths.IAM_AUTHORIZATION)
@Tag(name = "IAM - Authorization")
public class AuthorizationController {

    private final AuthorizationQueryService queryService;

    public AuthorizationController(AuthorizationQueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/check")
    @Operation(summary = "Check a permission/action on a concrete resource for the authenticated actor")
    public ApiResponse<AuthorizationExplanationResponse> check(
            @Valid @RequestBody CheckAuthorizationRequest request) {
        return ApiResponse.success(queryService.check(toQuery(request)));
    }

    @PostMapping("/check-batch")
    @Operation(summary = "Batch permission/action authorization checks")
    public ApiResponse<List<AuthorizationExplanationResponse>> checkBatch(
            @Valid @RequestBody CheckAuthorizationBatchRequest request) {
        return ApiResponse.success(request.checks().stream()
                .map(this::toQuery)
                .map(queryService::check)
                .toList());
    }

    @GetMapping("/explain")
    @Operation(summary = "Explain a permission/action authorization decision")
    public ApiResponse<AuthorizationExplanationResponse> explain(
            @Valid @ModelAttribute ExplainAuthorizationRequest request) {
        return ApiResponse.success(queryService.check(toQuery(request)));
    }

    @Operation(summary = "Check if a user has access to a resource by legacy right code")
    @PostMapping("/check-by-right")
    public ResponseEntity<ApiResponse<AuthorizationDecisionResponse>> checkByRight(
            @Valid @RequestBody CheckAccessRequest request) {
        return ResponseEntity.ok(ApiResponse.success(queryService.checkByRight(
                new CheckAccessByRightQuery(request.userId(), request.resourceId(), request.rightCode()))));
    }

    private CheckAuthorizationQuery toQuery(CheckAuthorizationRequest request) {
        return new CheckAuthorizationQuery(
                request.permissionCode(), request.actionCode(),
                request.resourceType(), request.resourceRefId());
    }

    private CheckAuthorizationQuery toQuery(ExplainAuthorizationRequest request) {
        return new CheckAuthorizationQuery(
                request.permissionCode(), request.actionCode(),
                request.resourceType(), request.resourceRefId());
    }
}
