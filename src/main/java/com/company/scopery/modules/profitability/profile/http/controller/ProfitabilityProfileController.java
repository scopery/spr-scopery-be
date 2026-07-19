package com.company.scopery.modules.profitability.profile.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.profile.application.action.CreateProfitabilityProfileAction;
import com.company.scopery.modules.profitability.profile.application.action.RebuildProfitabilitySummaryAction;
import com.company.scopery.modules.profitability.profile.application.response.*;
import com.company.scopery.modules.profitability.profile.application.service.ProfitabilityQueryService;
import com.company.scopery.modules.profitability.profile.http.request.CreateProfitabilityProfileRequest;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @Tag(name = "Profitability")
public class ProfitabilityProfileController {
    private final CreateProfitabilityProfileAction create; private final ProfitabilityQueryService query; private final RebuildProfitabilitySummaryAction rebuild;
    public ProfitabilityProfileController(CreateProfitabilityProfileAction create, ProfitabilityQueryService query, RebuildProfitabilitySummaryAction rebuild) {
        this.create=create; this.query=query; this.rebuild=rebuild;
    }
    @PostMapping(ProfitabilityApiPaths.PROFILE) @Operation(summary="Create profitability profile")
    public ApiResponse<ProjectProfitabilityProfileResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateProfitabilityProfileRequest r) {
        return ApiResponse.success(create.execute(projectId, r.currency()));
    }
    @GetMapping(ProfitabilityApiPaths.PROFILE) @Operation(summary="Get profitability profile")
    public ApiResponse<ProjectProfitabilityProfileResponse> get(@PathVariable UUID projectId) { return ApiResponse.success(query.getProfile(projectId)); }
    @GetMapping(ProfitabilityApiPaths.SUMMARY) @Operation(summary="Get profitability summary")
    public ApiResponse<ProfitabilitySummaryResponse> summary(@PathVariable UUID projectId) { return ApiResponse.success(query.summary(projectId)); }
    @GetMapping(ProfitabilityApiPaths.SUMMARY_PORTAL) @Operation(summary="Get portal-safe profitability summary when portal_visibility allows")
    public ApiResponse<ProfitabilityPortalSummaryResponse> portalSummary(@PathVariable UUID projectId) { return ApiResponse.success(query.portalSummary(projectId)); }
    @PostMapping(ProfitabilityApiPaths.SUMMARY_REBUILD) @Operation(summary="Rebuild profitability summary from quote/finance")
    public ApiResponse<ProfitabilitySummaryResponse> rebuild(@PathVariable UUID projectId) { return ApiResponse.success(rebuild.execute(projectId)); }
}
