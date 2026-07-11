package com.company.scopery.modules.iam.me.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.me.application.query.GetMeQuery;
import com.company.scopery.modules.iam.me.application.response.MeResponse;
import com.company.scopery.modules.iam.me.application.service.MeQueryService;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "IAM - Me")
@RestController
@RequestMapping(IamApiPaths.IAM_ME)
public class MeController {

    private final MeQueryService meQueryService;

    public MeController(MeQueryService meQueryService) {
        this.meQueryService = meQueryService;
    }

    @Operation(summary = "Get current authenticated user identity and memberships")
    @GetMapping
    public ResponseEntity<ApiResponse<MeResponse>> getMe() {
        return ResponseEntity.ok(ApiResponse.success(meQueryService.getMe(new GetMeQuery())));
    }
}
