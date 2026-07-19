package com.company.scopery.modules.profitability.threshold.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import com.company.scopery.modules.profitability.threshold.application.action.UpsertThresholdPolicyAction;
import com.company.scopery.modules.profitability.threshold.application.response.ProfitThresholdPolicyResponse;
import com.company.scopery.modules.profitability.threshold.application.service.ThresholdPolicyQueryService;
import com.company.scopery.modules.profitability.threshold.http.request.UpsertThresholdRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping(ProfitabilityApiPaths.THRESHOLD) @Tag(name = "Profitability - Thresholds")
public class ThresholdPolicyController {
    private final UpsertThresholdPolicyAction upsert; private final ThresholdPolicyQueryService query;
    public ThresholdPolicyController(UpsertThresholdPolicyAction upsert, ThresholdPolicyQueryService query) { this.upsert=upsert; this.query=query; }
    @GetMapping @Operation(summary="Get threshold policy")
    public ApiResponse<ProfitThresholdPolicyResponse> get(@PathVariable UUID projectId) { return ApiResponse.success(query.get(projectId)); }
    @PutMapping @Operation(summary="Upsert threshold policy")
    public ApiResponse<ProfitThresholdPolicyResponse> upsert(@PathVariable UUID projectId, @RequestBody UpsertThresholdRequest r) {
        return ApiResponse.success(upsert.execute(projectId, r.healthyMarginPercent(), r.watchMarginPercent(), r.atRiskMarginPercent(), r.lossRiskMarginPercent()));
    }
}
