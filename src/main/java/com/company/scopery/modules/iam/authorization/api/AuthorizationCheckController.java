package com.company.scopery.modules.iam.authorization.api;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.authorization.api.request.CheckAccessRequest;
import com.company.scopery.modules.iam.authorization.application.AuthorizationDecisionResponse;
import com.company.scopery.modules.iam.authorization.application.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.domain.AuthorizationDecision;
import com.company.scopery.modules.iam.authorization.domain.AuthorizationRequest;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "IAM - Authorization")
@RestController
@RequestMapping(IamApiPaths.IAM_AUTHORIZATION)
public class AuthorizationCheckController {

    private final AuthorizationDecisionService decisionService;

    public AuthorizationCheckController(AuthorizationDecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @Operation(summary = "Check if a user has access to a resource (debug endpoint)")
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<AuthorizationDecisionResponse>> check(
            @Valid @RequestBody CheckAccessRequest request) {
        AuthorizationDecision decision = decisionService.canAccess(
                new AuthorizationRequest(request.userId(), request.resourceId(), request.rightCode()));
        AuthorizationDecisionResponse response = AuthorizationDecisionResponse.from(
                request.userId(), request.resourceId(), request.rightCode(), decision);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
