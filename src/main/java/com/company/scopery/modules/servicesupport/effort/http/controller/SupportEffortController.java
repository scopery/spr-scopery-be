package com.company.scopery.modules.servicesupport.effort.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.effort.application.action.CancelSupportEffortAction;
import com.company.scopery.modules.servicesupport.effort.application.action.RecordSupportEffortAction;
import com.company.scopery.modules.servicesupport.effort.application.command.RecordSupportEffortCommand;
import com.company.scopery.modules.servicesupport.effort.application.response.SupportEffortResponse;
import com.company.scopery.modules.servicesupport.effort.application.service.SupportEffortQueryService;
import com.company.scopery.modules.servicesupport.effort.http.request.RecordSupportEffortRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Effort Records")
public class SupportEffortController {
    private final SupportEffortQueryService query;
    private final RecordSupportEffortAction recordAction;
    private final CancelSupportEffortAction cancelAction;

    public SupportEffortController(SupportEffortQueryService query, RecordSupportEffortAction recordAction,
            CancelSupportEffortAction cancelAction) {
        this.query = query; this.recordAction = recordAction; this.cancelAction = cancelAction;
    }

    @GetMapping(SupportApiPaths.CASE_EFFORTS)
    @Operation(summary = "List effort records for a case")
    public ApiResponse<List<SupportEffortResponse>> listByCase(@PathVariable UUID workspaceId,
            @PathVariable UUID caseId) {
        return ApiResponse.success(query.listByCase(workspaceId, caseId));
    }

    @GetMapping(SupportApiPaths.EFFORTS)
    @Operation(summary = "List all effort records in workspace")
    public ApiResponse<List<SupportEffortResponse>> listAll(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }

    @PostMapping(SupportApiPaths.CASE_EFFORTS)
    @Operation(summary = "Record effort on a case")
    public ApiResponse<SupportEffortResponse> record(@PathVariable UUID workspaceId,
            @PathVariable UUID caseId, @RequestBody @Valid RecordSupportEffortRequest req) {
        return ApiResponse.success(recordAction.execute(workspaceId,
                new RecordSupportEffortCommand(caseId, req.resourceProfileId(),
                        req.effortHours(), req.effortDate())));
    }

    @PostMapping(SupportApiPaths.EFFORT_CANCEL)
    @Operation(summary = "Cancel effort record")
    public ApiResponse<SupportEffortResponse> cancel(@PathVariable UUID workspaceId,
            @PathVariable UUID effortId) {
        return ApiResponse.success(cancelAction.execute(workspaceId, effortId));
    }
}
