package com.company.scopery.modules.workspace.member.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.workspace.member.application.query.WorkspaceDailySummaryQuery;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceDailySummaryResponse;
import com.company.scopery.modules.workspace.member.application.service.WorkspaceTaskSummaryQueryService;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Workspace - Daily Summary", description = "Daily task summary per workspace member")
@RestController
@RequestMapping(WorkspaceApiPaths.WORKSPACE_TASK_DAILY_SUMMARY)
public class WorkspaceTaskSummaryController {

    private final WorkspaceTaskSummaryQueryService queryService;

    public WorkspaceTaskSummaryController(WorkspaceTaskSummaryQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Get daily task summary for all active workspace members")
    @GetMapping
    public ResponseEntity<ApiResponse<WorkspaceDailySummaryResponse>> getDailySummary(
            @PathVariable UUID workspaceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        WorkspaceDailySummaryQuery query = new WorkspaceDailySummaryQuery(workspaceId, date);
        return ResponseEntity.ok(ApiResponse.success(queryService.getDailySummary(query)));
    }
}
