package com.company.scopery.modules.servicesupport.maintenance.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.maintenance.application.action.CreateMaintenanceActivityAction;
import com.company.scopery.modules.servicesupport.maintenance.application.command.CreateMaintenanceActivityCommand;
import com.company.scopery.modules.servicesupport.maintenance.application.response.MaintenanceActivityResponse;
import com.company.scopery.modules.servicesupport.maintenance.application.service.MaintenanceQueryService;
import com.company.scopery.modules.servicesupport.maintenance.http.request.CreateMaintenanceActivityRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Maintenance Activities")
public class MaintenanceActivityController {
    private final MaintenanceQueryService query;
    private final CreateMaintenanceActivityAction createAction;

    public MaintenanceActivityController(MaintenanceQueryService query, CreateMaintenanceActivityAction createAction) {
        this.query = query; this.createAction = createAction;
    }

    @GetMapping(SupportApiPaths.MAINTENANCE_ACTIVITIES)
    @Operation(summary = "List maintenance activities")
    public ApiResponse<List<MaintenanceActivityResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listActivities(workspaceId));
    }

    @PostMapping(SupportApiPaths.MAINTENANCE_ACTIVITIES)
    @Operation(summary = "Create maintenance activity")
    public ApiResponse<MaintenanceActivityResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateMaintenanceActivityRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateMaintenanceActivityCommand(req.maintenancePlanId(), req.activityType(), req.title())));
    }
}
