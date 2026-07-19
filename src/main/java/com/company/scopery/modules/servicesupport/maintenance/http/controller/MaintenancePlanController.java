package com.company.scopery.modules.servicesupport.maintenance.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.maintenance.application.action.CreateMaintenancePlanAction;
import com.company.scopery.modules.servicesupport.maintenance.application.command.CreateMaintenancePlanCommand;
import com.company.scopery.modules.servicesupport.maintenance.application.response.MaintenancePlanResponse;
import com.company.scopery.modules.servicesupport.maintenance.application.service.MaintenanceQueryService;
import com.company.scopery.modules.servicesupport.maintenance.http.request.CreateMaintenancePlanRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Maintenance Plans")
public class MaintenancePlanController {
    private final MaintenanceQueryService query;
    private final CreateMaintenancePlanAction createAction;

    public MaintenancePlanController(MaintenanceQueryService query, CreateMaintenancePlanAction createAction) {
        this.query = query; this.createAction = createAction;
    }

    @GetMapping(SupportApiPaths.MAINTENANCE_PLANS)
    @Operation(summary = "List maintenance plans")
    public ApiResponse<List<MaintenancePlanResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listPlans(workspaceId));
    }

    @PostMapping(SupportApiPaths.MAINTENANCE_PLANS)
    @Operation(summary = "Create maintenance plan")
    public ApiResponse<MaintenancePlanResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateMaintenancePlanRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateMaintenancePlanCommand(req.name(), req.projectId(), req.plannedStart(), req.plannedEnd())));
    }
}
