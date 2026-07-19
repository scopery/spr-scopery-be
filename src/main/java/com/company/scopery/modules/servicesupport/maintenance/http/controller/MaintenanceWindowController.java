package com.company.scopery.modules.servicesupport.maintenance.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.maintenance.application.action.CreateMaintenanceWindowAction;
import com.company.scopery.modules.servicesupport.maintenance.application.command.CreateMaintenanceWindowCommand;
import com.company.scopery.modules.servicesupport.maintenance.application.response.MaintenanceWindowResponse;
import com.company.scopery.modules.servicesupport.maintenance.application.service.MaintenanceQueryService;
import com.company.scopery.modules.servicesupport.maintenance.http.request.CreateMaintenanceWindowRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Maintenance Windows")
public class MaintenanceWindowController {
    private final MaintenanceQueryService query;
    private final CreateMaintenanceWindowAction createAction;

    public MaintenanceWindowController(MaintenanceQueryService query, CreateMaintenanceWindowAction createAction) {
        this.query = query; this.createAction = createAction;
    }

    @GetMapping(SupportApiPaths.MAINTENANCE_WINDOWS)
    @Operation(summary = "List maintenance windows")
    public ApiResponse<List<MaintenanceWindowResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listWindows(workspaceId));
    }

    @PostMapping(SupportApiPaths.MAINTENANCE_WINDOWS)
    @Operation(summary = "Create maintenance window")
    public ApiResponse<MaintenanceWindowResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateMaintenanceWindowRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateMaintenanceWindowCommand(req.maintenancePlanId(), req.scheduledStart(),
                        req.scheduledEnd(), req.title())));
    }
}
