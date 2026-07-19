package com.company.scopery.modules.servicesupport.sla.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import com.company.scopery.modules.servicesupport.sla.application.action.CreateSlaTargetAction;
import com.company.scopery.modules.servicesupport.sla.application.command.CreateSlaTargetCommand;
import com.company.scopery.modules.servicesupport.sla.application.response.SlaTargetResponse;
import com.company.scopery.modules.servicesupport.sla.application.service.SlaQueryService;
import com.company.scopery.modules.servicesupport.sla.http.request.CreateSlaTargetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - SLA Targets")
public class SlaTargetController {
    private final SlaQueryService query;
    private final CreateSlaTargetAction createAction;

    public SlaTargetController(SlaQueryService query, CreateSlaTargetAction createAction) {
        this.query = query; this.createAction = createAction;
    }

    @GetMapping(SupportApiPaths.SLA_TARGETS)
    @Operation(summary = "List SLA targets")
    public ApiResponse<List<SlaTargetResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listTargets(workspaceId));
    }

    @PostMapping(SupportApiPaths.SLA_TARGETS)
    @Operation(summary = "Create SLA target")
    public ApiResponse<SlaTargetResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateSlaTargetRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateSlaTargetCommand(req.slaPolicyId(), req.targetType(),
                        req.durationMinutes(), req.requestTypeId(), req.priority())));
    }
}
