package com.company.scopery.modules.iam.access.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.access.application.query.ExplainAccessQuery;
import com.company.scopery.modules.iam.access.application.service.IamAccessQueryService;
import com.company.scopery.modules.iam.access.http.request.ExplainAccessRequest;
import com.company.scopery.modules.iam.authorization.application.response.AuthorizationExplanationResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(IamApiPaths.IAM_ACCESS)
@Tag(name = "IAM - Effective Access")
public class IamAccessController {

    private final IamAccessQueryService queryService;

    public IamAccessController(IamAccessQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/explain")
    @Operation(summary = "Explain effective access for the authenticated actor")
    public ApiResponse<AuthorizationExplanationResponse> explain(
            @Valid @ModelAttribute ExplainAccessRequest request) {
        return ApiResponse.success(queryService.explainAccess(new ExplainAccessQuery(
                request.permissionCode(), request.actionCode(),
                request.resourceType(), request.resourceRefId())));
    }
}
