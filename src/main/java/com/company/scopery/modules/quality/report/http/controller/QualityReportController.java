package com.company.scopery.modules.quality.report.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.report.application.service.QualityReportQueryService;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @RequestMapping(QualityApiPaths.REPORTS) @Tag(name="Quality - Reports")
public class QualityReportController {
    private final QualityReportQueryService query;
    public QualityReportController(QualityReportQueryService query) { this.query = query; }
    @GetMapping("/quality-dashboard") @Operation(summary="Quality dashboard")
    public ApiResponse<Map<String, Object>> dashboard(@PathVariable UUID projectId) { return ApiResponse.success(query.qualityDashboard(projectId)); }
    @GetMapping("/defects") @Operation(summary="Defects report")
    public ApiResponse<Map<String, Object>> defects(@PathVariable UUID projectId) { return ApiResponse.success(query.defectsReport(projectId)); }
    @GetMapping("/release-readiness") @Operation(summary="Release readiness report")
    public ApiResponse<Map<String, Object>> releaseReadiness(@PathVariable UUID projectId) { return ApiResponse.success(query.releaseReadiness(projectId)); }
    @GetMapping("/test-execution") @Operation(summary="Test execution report")
    public ApiResponse<Map<String, Object>> testExecution(@PathVariable UUID projectId) { return ApiResponse.success(query.testExecution(projectId)); }
    @GetMapping("/test-coverage") @Operation(summary="Test coverage report")
    public ApiResponse<Map<String, Object>> testCoverage(@PathVariable UUID projectId) { return ApiResponse.success(Map.of("projectId", projectId.toString(), "coverage", "foundation")); }
    @GetMapping("/defect-aging") @Operation(summary="Defect aging report")
    public ApiResponse<Map<String, Object>> defectAging(@PathVariable UUID projectId) { return ApiResponse.success(query.defectsReport(projectId)); }
    @GetMapping("/deployment-history") @Operation(summary="Deployment history report")
    public ApiResponse<Map<String, Object>> deploymentHistory(@PathVariable UUID projectId) { return ApiResponse.success(Map.of("projectId", projectId.toString(), "deployments", List.of())); }
}
