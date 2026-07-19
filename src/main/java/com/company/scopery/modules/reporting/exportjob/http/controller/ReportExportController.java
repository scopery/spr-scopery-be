package com.company.scopery.modules.reporting.exportjob.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.reporting.exportjob.application.action.CancelReportExportAction;
import com.company.scopery.modules.reporting.exportjob.application.command.CancelReportExportCommand;
import com.company.scopery.modules.reporting.exportjob.application.response.ReportExportJobResponse;
import com.company.scopery.modules.reporting.exportjob.application.service.ReportExportQueryService;
import com.company.scopery.modules.reporting.exportjob.domain.model.ReportExportJob;
import com.company.scopery.modules.reporting.shared.constant.ReportingApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ReportingApiPaths.EXPORTS)
@Tag(name = "Reporting - Exports")
public class ReportExportController {
    private final ReportExportQueryService queryService;
    private final CancelReportExportAction cancel;

    public ReportExportController(ReportExportQueryService queryService, CancelReportExportAction cancel) {
        this.queryService = queryService;
        this.cancel = cancel;
    }

    @GetMapping
    @Operation(summary = "List report exports for a project")
    public ApiResponse<List<ReportExportJobResponse>> list(@RequestParam UUID projectId) {
        return ApiResponse.success(queryService.listByProject(projectId));
    }

    @GetMapping("/{exportJobId}")
    @Operation(summary = "Get report export job")
    public ApiResponse<ReportExportJobResponse> get(@PathVariable UUID exportJobId) {
        return ApiResponse.success(queryService.getById(exportJobId));
    }

    @GetMapping("/{exportJobId}/download")
    @Operation(summary = "Download report export content")
    public ResponseEntity<byte[]> download(@PathVariable UUID exportJobId) {
        ReportExportJob job = queryService.requireDownloadable(exportJobId);
        MediaType mediaType = job.fileMimeType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(job.fileMimeType());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + job.fileName() + "\"")
                .contentType(mediaType)
                .body(job.contentText().getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/{exportJobId}/cancel")
    @Operation(summary = "Cancel a pending/running report export")
    public ApiResponse<ReportExportJobResponse> cancel(@PathVariable UUID exportJobId) {
        return ApiResponse.success(cancel.execute(new CancelReportExportCommand(exportJobId)));
    }
}
