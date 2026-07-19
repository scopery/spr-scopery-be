package com.company.scopery.modules.servicesupport.incident.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.incident.application.action.*;
import com.company.scopery.modules.servicesupport.incident.application.command.*;
import com.company.scopery.modules.servicesupport.incident.application.response.*;
import com.company.scopery.modules.servicesupport.incident.application.service.SupportIncidentQueryService;
import com.company.scopery.modules.servicesupport.incident.http.request.*;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Incidents")
public class SupportIncidentController {
    private final SupportIncidentQueryService query;
    private final CreateIncidentAction createAction;
    private final AcknowledgeIncidentAction acknowledgeAction;
    private final ResolveIncidentAction resolveAction;
    private final CloseIncidentAction closeAction;
    private final AddIncidentTimelineEntryAction addTimelineAction;

    public SupportIncidentController(SupportIncidentQueryService query, CreateIncidentAction createAction,
            AcknowledgeIncidentAction acknowledgeAction, ResolveIncidentAction resolveAction,
            CloseIncidentAction closeAction, AddIncidentTimelineEntryAction addTimelineAction) {
        this.query = query; this.createAction = createAction; this.acknowledgeAction = acknowledgeAction;
        this.resolveAction = resolveAction; this.closeAction = closeAction; this.addTimelineAction = addTimelineAction;
    }

    @GetMapping(SupportApiPaths.INCIDENTS)
    @Operation(summary = "List incidents in workspace")
    public ApiResponse<List<SupportIncidentResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }

    @PostMapping(SupportApiPaths.INCIDENTS)
    @Operation(summary = "Create incident")
    public ApiResponse<SupportIncidentResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateIncidentRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateIncidentCommand(req.title(), req.severity(), req.projectId())));
    }

    @PostMapping(SupportApiPaths.INCIDENT_ACKNOWLEDGE)
    @Operation(summary = "Acknowledge incident")
    public ApiResponse<SupportIncidentResponse> acknowledge(@PathVariable UUID workspaceId,
            @PathVariable UUID incidentId) {
        return ApiResponse.success(acknowledgeAction.execute(workspaceId, incidentId));
    }

    @PostMapping(SupportApiPaths.INCIDENT_RESOLVE)
    @Operation(summary = "Resolve incident")
    public ApiResponse<SupportIncidentResponse> resolve(@PathVariable UUID workspaceId,
            @PathVariable UUID incidentId, @RequestBody @Valid ResolveIncidentRequest req) {
        return ApiResponse.success(resolveAction.execute(workspaceId, incidentId,
                new ResolveIncidentCommand(req.impactSummary())));
    }

    @PostMapping(SupportApiPaths.INCIDENT_CLOSE)
    @Operation(summary = "Close incident")
    public ApiResponse<SupportIncidentResponse> close(@PathVariable UUID workspaceId,
            @PathVariable UUID incidentId) {
        return ApiResponse.success(closeAction.execute(workspaceId, incidentId));
    }

    @GetMapping(SupportApiPaths.INCIDENT_TIMELINE)
    @Operation(summary = "List incident timeline")
    public ApiResponse<List<IncidentTimelineEntryResponse>> listTimeline(@PathVariable UUID workspaceId,
            @PathVariable UUID incidentId) {
        return ApiResponse.success(query.listTimeline(workspaceId, incidentId));
    }

    @PostMapping(SupportApiPaths.INCIDENT_TIMELINE)
    @Operation(summary = "Add incident timeline entry")
    public ApiResponse<IncidentTimelineEntryResponse> addTimeline(@PathVariable UUID workspaceId,
            @PathVariable UUID incidentId, @RequestBody @Valid AddIncidentTimelineEntryRequest req) {
        return ApiResponse.success(addTimelineAction.execute(workspaceId, incidentId,
                new AddIncidentTimelineEntryCommand(req.entryType(), req.message(), req.visibility())));
    }
}
