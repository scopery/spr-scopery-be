package com.company.scopery.modules.scope.report.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.scope.report.application.response.*;
import com.company.scopery.modules.scope.report.application.service.ScopeReportQueryService;
import com.company.scopery.modules.scope.shared.constant.ScopeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping(ScopeApiPaths.REPORTS)
@Tag(name = "Scope - Reports")
public class ScopeReportController {
    private final ScopeReportQueryService query;
    public ScopeReportController(ScopeReportQueryService query) { this.query = query; }
    @GetMapping("/scope-coverage")
    @Operation(summary = "Scope coverage summary")
    public ApiResponse<ScopeCoverageReportResponse> scopeCoverage(@PathVariable UUID projectId) {
        return ApiResponse.success(query.scopeCoverage(projectId));
    }
    @GetMapping("/deliverable-status")
    @Operation(summary = "Deliverable status counts")
    public ApiResponse<DeliverableStatusReportResponse> deliverableStatus(@PathVariable UUID projectId) {
        return ApiResponse.success(query.deliverableStatus(projectId));
    }
    @GetMapping("/acceptance-criteria")
    @Operation(summary = "Acceptance criteria status counts")
    public ApiResponse<AcceptanceCriteriaReportResponse> acceptanceCriteria(@PathVariable UUID projectId) {
        return ApiResponse.success(query.acceptanceCriteria(projectId));
    }
    @GetMapping("/acceptance-evidence")
    @Operation(summary = "Acceptance evidence summary")
    public ApiResponse<AcceptanceEvidenceReportResponse> acceptanceEvidence(@PathVariable UUID projectId) {
        return ApiResponse.success(query.acceptanceEvidence(projectId));
    }
}
