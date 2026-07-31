package com.company.scopery.modules.traceability.coverage.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.coverage.application.response.*;
import com.company.scopery.modules.traceability.coverage.application.service.TraceabilityCoverageQueryService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Traceability - Coverage")
public class TraceabilityCoverageController {

    private final TraceabilityCoverageQueryService service;

    public TraceabilityCoverageController(TraceabilityCoverageQueryService service) {
        this.service = service;
    }

    @GetMapping(TraceabilityApiPaths.TRACEABILITY_COVERAGE_SUMMARY)
    @Operation(summary = "Get traceability coverage summary for a project")
    public ApiResponse<CoverageSummaryResponse> coverageSummary(@PathVariable UUID projectId) {
        return ApiResponse.success(service.getCoverageSummary(projectId));
    }

    @GetMapping(TraceabilityApiPaths.TRACEABILITY_MATRIX)
    @Operation(summary = "Get traceability coverage matrix with per-requirement gap analysis")
    public ApiResponse<TraceabilityMatrixResponse> matrix(
            @PathVariable UUID projectId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String coverageStatus,
            @RequestParam(required = false) String gapCode,
            @RequestParam(required = false) String requirementType,
            @RequestParam(defaultValue = "false") boolean showGapsOnly,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ApiResponse.success(service.getMatrix(projectId, q, coverageStatus, gapCode, requirementType, showGapsOnly, limit, offset));
    }

    @GetMapping(TraceabilityApiPaths.TRACEABILITY_REQ_DETAIL)
    @Operation(summary = "Get full traceability detail for a single requirement")
    public ApiResponse<RequirementTraceDetailResponse> requirementDetail(
            @PathVariable UUID projectId,
            @PathVariable UUID requirementId) {
        return ApiResponse.success(service.getRequirementDetail(projectId, requirementId));
    }

    @GetMapping(TraceabilityApiPaths.TRACEABILITY_GAPS)
    @Operation(summary = "List all traceability gaps across requirements")
    public ApiResponse<GapsResponse> gaps(
            @PathVariable UUID projectId,
            @RequestParam(required = false) String gapCode,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String requirementId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ApiResponse.success(service.getGaps(projectId, gapCode, priority, requirementId, q, limit, offset));
    }

    @GetMapping(TraceabilityApiPaths.TRACEABILITY_REQ_HISTORY)
    @Operation(summary = "Get activity history for a single requirement")
    public ApiResponse<RequirementTraceHistoryResponse> requirementHistory(
            @PathVariable UUID projectId,
            @PathVariable UUID requirementId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ApiResponse.success(service.getRequirementHistory(projectId, requirementId, limit, offset));
    }
}
