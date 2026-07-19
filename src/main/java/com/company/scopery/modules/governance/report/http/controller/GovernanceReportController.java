package com.company.scopery.modules.governance.report.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.governance.report.application.response.*;
import com.company.scopery.modules.governance.report.application.service.GovernanceReportQueryService;
import com.company.scopery.modules.governance.shared.constant.GovernanceApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping(GovernanceApiPaths.REPORTS)
@Tag(name = "Governance - Reports")
public class GovernanceReportController {
    private final GovernanceReportQueryService query;
    public GovernanceReportController(GovernanceReportQueryService query) { this.query = query; }

    @GetMapping("/pack")
    @Operation(summary = "Governance report pack — aggregate summary (ownership/locks/grants counts)")
    public ApiResponse<GovernanceReportPackResponse> pack(@PathVariable UUID projectId) {
        return ApiResponse.success(query.pack(projectId));
    }

    @GetMapping("/ownership")
    @Operation(summary = "Ownership report — all object ownership records for a project")
    public ApiResponse<GovernanceOwnershipReportResponse> ownershipReport(@PathVariable UUID projectId) {
        return ApiResponse.success(query.ownershipReport(projectId));
    }

    @GetMapping("/access-grants")
    @Operation(summary = "Access grant report — all object access grants for a project")
    public ApiResponse<GovernanceAccessGrantReportResponse> accessGrantReport(@PathVariable UUID projectId) {
        return ApiResponse.success(query.accessGrantReport(projectId));
    }

    @GetMapping("/version-history")
    @Operation(summary = "Version history report — all version records for a project")
    public ApiResponse<GovernanceVersionHistoryReportResponse> versionHistoryReport(@PathVariable UUID projectId) {
        return ApiResponse.success(query.versionHistoryReport(projectId));
    }

    @GetMapping("/locked-objects")
    @Operation(summary = "Locked objects report — all object locks for a project")
    public ApiResponse<GovernanceLockedObjectsReportResponse> lockedObjectsReport(@PathVariable UUID projectId) {
        return ApiResponse.success(query.lockedObjectsReport(projectId));
    }

    @GetMapping("/restore-activity")
    @Operation(summary = "Restore activity report — all restore requests for a project")
    public ApiResponse<GovernanceRestoreActivityReportResponse> restoreActivityReport(@PathVariable UUID projectId) {
        return ApiResponse.success(query.restoreActivityReport(projectId));
    }
}
