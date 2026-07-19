package com.company.scopery.modules.servicesupport.assignment.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.assignment.application.action.AssignSupportCaseAction;
import com.company.scopery.modules.servicesupport.assignment.application.command.AssignSupportCaseCommand;
import com.company.scopery.modules.servicesupport.assignment.application.response.SupportAssignmentResponse;
import com.company.scopery.modules.servicesupport.assignment.application.service.SupportAssignmentQueryService;
import com.company.scopery.modules.servicesupport.assignment.http.request.AssignSupportCaseRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Assignments")
public class SupportAssignmentController {
    private final SupportAssignmentQueryService query;
    private final AssignSupportCaseAction assignAction;

    public SupportAssignmentController(SupportAssignmentQueryService query, AssignSupportCaseAction assignAction) {
        this.query = query; this.assignAction = assignAction;
    }

    @GetMapping(SupportApiPaths.CASE_ASSIGNMENTS)
    @Operation(summary = "List assignments for a case")
    public ApiResponse<List<SupportAssignmentResponse>> list(@PathVariable UUID workspaceId,
            @PathVariable UUID caseId) {
        return ApiResponse.success(query.listByCase(workspaceId, caseId));
    }

    @PostMapping(SupportApiPaths.CASE_ASSIGNMENTS)
    @Operation(summary = "Assign a case")
    public ApiResponse<SupportAssignmentResponse> assign(@PathVariable UUID workspaceId,
            @PathVariable UUID caseId, @RequestBody @Valid AssignSupportCaseRequest req) {
        return ApiResponse.success(assignAction.execute(workspaceId, caseId,
                new AssignSupportCaseCommand(req.assigneeUserId(), req.resourceProfileId())));
    }
}
