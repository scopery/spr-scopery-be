package com.company.scopery.modules.servicesupport.costinput.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.costinput.application.action.ApproveServiceCostInputAction;
import com.company.scopery.modules.servicesupport.costinput.application.action.CreateServiceCostInputAction;
import com.company.scopery.modules.servicesupport.costinput.application.command.CreateServiceCostInputCommand;
import com.company.scopery.modules.servicesupport.costinput.application.response.ServiceCostInputResponse;
import com.company.scopery.modules.servicesupport.costinput.application.service.ServiceCostInputQueryService;
import com.company.scopery.modules.servicesupport.costinput.http.request.CreateServiceCostInputRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Cost Inputs")
public class ServiceCostInputController {
    private final ServiceCostInputQueryService query;
    private final CreateServiceCostInputAction createAction;
    private final ApproveServiceCostInputAction approveAction;

    public ServiceCostInputController(ServiceCostInputQueryService query,
            CreateServiceCostInputAction createAction, ApproveServiceCostInputAction approveAction) {
        this.query = query; this.createAction = createAction; this.approveAction = approveAction;
    }

    @GetMapping(SupportApiPaths.COST_INPUTS)
    @Operation(summary = "List cost inputs in workspace")
    public ApiResponse<List<ServiceCostInputResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }

    @PostMapping(SupportApiPaths.COST_INPUTS)
    @Operation(summary = "Create cost input")
    public ApiResponse<ServiceCostInputResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateServiceCostInputRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateServiceCostInputCommand(req.supportCaseId(), req.sourceType(),
                        req.costAmount(), req.currency())));
    }

    @PostMapping(SupportApiPaths.COST_INPUT_APPROVE)
    @Operation(summary = "Approve cost input")
    public ApiResponse<ServiceCostInputResponse> approve(@PathVariable UUID workspaceId,
            @PathVariable UUID inputId) {
        return ApiResponse.success(approveAction.execute(workspaceId, inputId));
    }
}
