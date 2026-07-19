package com.company.scopery.modules.servicesupport.sla.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import com.company.scopery.modules.servicesupport.sla.application.action.CreateSlaPolicyAction;
import com.company.scopery.modules.servicesupport.sla.application.command.CreateSlaPolicyCommand;
import com.company.scopery.modules.servicesupport.sla.application.response.*;
import com.company.scopery.modules.servicesupport.sla.application.service.SlaQueryService;
import com.company.scopery.modules.servicesupport.sla.http.request.CreateSlaPolicyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - SLA")
public class SlaController {
    private final SlaQueryService query;
    private final CreateSlaPolicyAction createPolicyAction;

    public SlaController(SlaQueryService query, CreateSlaPolicyAction createPolicyAction) {
        this.query = query; this.createPolicyAction = createPolicyAction;
    }

    @GetMapping(SupportApiPaths.SLA_POLICIES)
    @Operation(summary = "List SLA policies")
    public ApiResponse<List<SlaPolicyResponse>> listPolicies(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listPolicies(workspaceId));
    }

    @PostMapping(SupportApiPaths.SLA_POLICIES)
    @Operation(summary = "Create SLA policy")
    public ApiResponse<SlaPolicyResponse> createPolicy(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateSlaPolicyRequest req) {
        return ApiResponse.success(createPolicyAction.execute(workspaceId,
                new CreateSlaPolicyCommand(req.policyCode(), req.name(),
                        req.firstResponseMinutes(), req.resolveMinutes())));
    }

    @GetMapping(SupportApiPaths.SLA_CLOCKS)
    @Operation(summary = "List active SLA clocks")
    public ApiResponse<List<SlaClockResponse>> listClocks(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listClocks(workspaceId));
    }

    @GetMapping(SupportApiPaths.SLA_BREACHES)
    @Operation(summary = "List SLA breaches")
    public ApiResponse<List<SlaBreachResponse>> listBreaches(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listBreaches(workspaceId));
    }
}
