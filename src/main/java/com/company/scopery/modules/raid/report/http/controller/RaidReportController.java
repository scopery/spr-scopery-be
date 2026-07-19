package com.company.scopery.modules.raid.report.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemType;
import com.company.scopery.modules.raid.report.application.service.RaidReportQueryService;
import com.company.scopery.modules.raid.shared.constant.RaidApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @RequestMapping(RaidApiPaths.REPORTS) @Tag(name="RAID - Reports")
public class RaidReportController {
    private final RaidReportQueryService reports;
    public RaidReportController(RaidReportQueryService reports) { this.reports = reports; }
    @GetMapping("/raid-summary") @Operation(summary="RAID summary")
    public ApiResponse<Map<String, Object>> summary(@PathVariable UUID projectId) { return ApiResponse.success(reports.summary(projectId)); }
    @GetMapping("/risk-register") @Operation(summary="Risk register")
    public ApiResponse<List<RaidItemResponse>> risks(@PathVariable UUID projectId) { return ApiResponse.success(reports.byType(projectId, RaidItemType.RISK)); }
    @GetMapping("/issue-log") @Operation(summary="Issue log")
    public ApiResponse<List<RaidItemResponse>> issues(@PathVariable UUID projectId) { return ApiResponse.success(reports.byType(projectId, RaidItemType.ISSUE)); }
    @GetMapping("/assumption-log") @Operation(summary="Assumption log")
    public ApiResponse<List<RaidItemResponse>> assumptions(@PathVariable UUID projectId) { return ApiResponse.success(reports.byType(projectId, RaidItemType.ASSUMPTION)); }
    @GetMapping("/dependency-log") @Operation(summary="Dependency log")
    public ApiResponse<List<RaidItemResponse>> dependencies(@PathVariable UUID projectId) { return ApiResponse.success(reports.byType(projectId, RaidItemType.DEPENDENCY)); }
    @GetMapping("/raid-actions") @Operation(summary="RAID actions report")
    public ApiResponse<List<Map<String, Object>>> actions(@PathVariable UUID projectId) { return ApiResponse.success(reports.actions(projectId)); }
    @GetMapping("/decision-log") @Operation(summary="Decision log")
    public ApiResponse<List<Map<String, Object>>> decisions(@PathVariable UUID projectId) { return ApiResponse.success(reports.decisionLog(projectId)); }
}
