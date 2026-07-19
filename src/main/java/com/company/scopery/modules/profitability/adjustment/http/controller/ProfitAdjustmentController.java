package com.company.scopery.modules.profitability.adjustment.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.adjustment.application.action.ApplyProfitAdjustmentAction;
import com.company.scopery.modules.profitability.adjustment.application.action.CreateProfitAdjustmentAction;
import com.company.scopery.modules.profitability.adjustment.application.response.ProfitAdjustmentResponse;
import com.company.scopery.modules.profitability.adjustment.application.service.ProfitAdjustmentQueryService;
import com.company.scopery.modules.profitability.adjustment.http.request.CreateAdjustmentRequest;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ProfitabilityApiPaths.ADJUSTMENTS) @Tag(name = "Profitability - Adjustments")
public class ProfitAdjustmentController {
    private final CreateProfitAdjustmentAction create;
    private final ApplyProfitAdjustmentAction apply;
    private final ProfitAdjustmentQueryService query;
    public ProfitAdjustmentController(CreateProfitAdjustmentAction create, ApplyProfitAdjustmentAction apply, ProfitAdjustmentQueryService query) {
        this.create=create; this.apply=apply; this.query=query;
    }
    @PostMapping @Operation(summary="Create adjustment")
    public ApiResponse<ProfitAdjustmentResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateAdjustmentRequest r) {
        return ApiResponse.success(create.execute(projectId, r.adjustmentType(), r.amount(), r.reason()));
    }
    @PostMapping("/{adjustmentId}/apply") @Operation(summary="Apply adjustment and request summary rebuild")
    public ApiResponse<ProfitAdjustmentResponse> apply(@PathVariable UUID projectId, @PathVariable UUID adjustmentId) {
        return ApiResponse.success(apply.execute(projectId, adjustmentId));
    }
    @GetMapping @Operation(summary="List adjustments")
    public ApiResponse<List<ProfitAdjustmentResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
}
