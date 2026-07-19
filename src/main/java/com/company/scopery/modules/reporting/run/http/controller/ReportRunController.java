package com.company.scopery.modules.reporting.run.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.reporting.exportjob.application.action.CreateReportExportAction;
import com.company.scopery.modules.reporting.exportjob.application.command.CreateReportExportCommand;
import com.company.scopery.modules.reporting.exportjob.application.response.CreateReportExportResponse;
import com.company.scopery.modules.reporting.exportjob.http.request.CreateReportExportRequest;
import com.company.scopery.modules.reporting.run.application.action.CreateReportRunAction;
import com.company.scopery.modules.reporting.run.application.command.CreateReportRunCommand;
import com.company.scopery.modules.reporting.run.application.response.CreateReportRunResponse;
import com.company.scopery.modules.reporting.run.application.response.ReportRunResponse;
import com.company.scopery.modules.reporting.run.application.service.ReportRunQueryService;
import com.company.scopery.modules.reporting.run.http.request.CreateReportRunRequest;
import com.company.scopery.modules.reporting.shared.constant.ReportingApiPaths;
import com.company.scopery.modules.reporting.snapshot.application.response.ReportSnapshotResponse;
import com.company.scopery.modules.reporting.snapshot.application.service.ReportSnapshotQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ReportingApiPaths.RUNS)
@Tag(name = "Reporting - Runs")
public class ReportRunController {
    private final CreateReportRunAction create;
    private final CreateReportExportAction export;
    private final ReportRunQueryService runQuery;
    private final ReportSnapshotQueryService snapshotQuery;

    public ReportRunController(CreateReportRunAction create, CreateReportExportAction export,
                               ReportRunQueryService runQuery, ReportSnapshotQueryService snapshotQuery) {
        this.create = create;
        this.export = export;
        this.runQuery = runQuery;
        this.snapshotQuery = snapshotQuery;
    }

    @PostMapping
    @Operation(summary = "Create report run")
    public ApiResponse<CreateReportRunResponse> create(@Valid @RequestBody CreateReportRunRequest request) {
        return ApiResponse.success(create.execute(new CreateReportRunCommand(
                request.reportCode(),
                request.projectId(),
                request.filters(),
                request.selectedFields())));
    }

    @GetMapping("/{reportRunId}")
    @Operation(summary = "Get report run")
    public ApiResponse<ReportRunResponse> get(@PathVariable UUID reportRunId) {
        return ApiResponse.success(runQuery.getById(reportRunId));
    }

    @GetMapping("/{reportRunId}/snapshot")
    @Operation(summary = "Get report snapshot")
    public ApiResponse<ReportSnapshotResponse> snapshot(@PathVariable UUID reportRunId) {
        return ApiResponse.success(snapshotQuery.getByReportRunId(reportRunId));
    }

    @PostMapping("/{reportRunId}/exports")
    @Operation(summary = "Create report export")
    public ApiResponse<CreateReportExportResponse> export(
            @PathVariable UUID reportRunId,
            @Valid @RequestBody CreateReportExportRequest request) {
        return ApiResponse.success(export.execute(new CreateReportExportCommand(
                reportRunId, request.format(), request.fileName())));
    }
}
