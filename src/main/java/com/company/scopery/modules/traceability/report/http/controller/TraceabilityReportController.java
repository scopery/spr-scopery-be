package com.company.scopery.modules.traceability.report.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.report.application.response.TraceCoverageMatrixResponse;
import com.company.scopery.modules.traceability.report.application.service.TraceabilityReportService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.REPORTS)
@Tag(name = "Traceability - Reports")
public class TraceabilityReportController {
    private final TraceabilityReportService reports;
    public TraceabilityReportController(TraceabilityReportService reports) { this.reports = reports; }
    @GetMapping("/coverage-matrix") @Operation(summary = "Coverage / gap matrix")
    public ApiResponse<TraceCoverageMatrixResponse> coverageMatrix(@PathVariable UUID projectId) {
        return ApiResponse.success(reports.coverageMatrix(projectId));
    }
}
