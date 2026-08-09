package com.company.scopery.modules.profitability.thresholdpolicy.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.thresholdpolicy.application.action.UpsertProfitThresholdPolicyAction;
import com.company.scopery.modules.profitability.thresholdpolicy.application.command.UpsertProfitThresholdPolicyCommand;
import com.company.scopery.modules.profitability.thresholdpolicy.application.response.ProfitThresholdPolicyResponse;
import com.company.scopery.modules.profitability.thresholdpolicy.application.service.ProfitThresholdPolicyQueryService;
import com.company.scopery.modules.profitability.thresholdpolicy.http.request.UpdateProfitThresholdPolicyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/profitability/threshold-policy")
@Tag(name = "Profitability - Threshold Policy")
public class ProfitThresholdPolicyController {

    private final UpsertProfitThresholdPolicyAction upsertPolicy;
    private final ProfitThresholdPolicyQueryService queryService;

    public ProfitThresholdPolicyController(UpsertProfitThresholdPolicyAction upsertPolicy,
                                           ProfitThresholdPolicyQueryService queryService) {
        this.upsertPolicy = upsertPolicy;
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "Get profitability threshold policy for a project")
    public ResponseEntity<ApiResponse<ProfitThresholdPolicyResponse>> getPolicy(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getPolicy(projectId)));
    }

    @PutMapping
    @Operation(summary = "Upsert profitability threshold policy for a project")
    public ResponseEntity<ApiResponse<ProfitThresholdPolicyResponse>> upsertPolicy(
            @PathVariable UUID projectId,
            @Valid @RequestBody UpdateProfitThresholdPolicyRequest request) {
        UpsertProfitThresholdPolicyCommand command = new UpsertProfitThresholdPolicyCommand(
                projectId,
                request.healthyMarginPercent(),
                request.watchMarginPercent(),
                request.atRiskMarginPercent(),
                request.lossRiskMarginPercent()
        );
        return ResponseEntity.ok(ApiResponse.success(upsertPolicy.execute(command)));
    }
}
