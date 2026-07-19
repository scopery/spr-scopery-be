package com.company.scopery.modules.servicesupport.warranty.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import com.company.scopery.modules.servicesupport.warranty.application.action.CreateWarrantyCoverageAction;
import com.company.scopery.modules.servicesupport.warranty.application.action.ExpireWarrantyCoverageAction;
import com.company.scopery.modules.servicesupport.warranty.application.command.CreateWarrantyCoverageCommand;
import com.company.scopery.modules.servicesupport.warranty.application.response.WarrantyCoverageResponse;
import com.company.scopery.modules.servicesupport.warranty.application.service.WarrantyCoverageQueryService;
import com.company.scopery.modules.servicesupport.warranty.http.request.CreateWarrantyCoverageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Warranty Coverage")
public class WarrantyCoverageController {
    private final WarrantyCoverageQueryService query;
    private final CreateWarrantyCoverageAction createAction;
    private final ExpireWarrantyCoverageAction expireAction;

    public WarrantyCoverageController(WarrantyCoverageQueryService query,
            CreateWarrantyCoverageAction createAction, ExpireWarrantyCoverageAction expireAction) {
        this.query = query; this.createAction = createAction; this.expireAction = expireAction;
    }

    @GetMapping(SupportApiPaths.WARRANTIES)
    @Operation(summary = "List warranty coverages")
    public ApiResponse<List<WarrantyCoverageResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }

    @PostMapping(SupportApiPaths.WARRANTIES)
    @Operation(summary = "Create warranty coverage")
    public ApiResponse<WarrantyCoverageResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateWarrantyCoverageRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateWarrantyCoverageCommand(req.projectId(), req.serviceProfileId(),
                        req.startDate(), req.endDate())));
    }

    @PostMapping(SupportApiPaths.WARRANTY_EXPIRE)
    @Operation(summary = "Expire warranty coverage")
    public ApiResponse<WarrantyCoverageResponse> expire(@PathVariable UUID workspaceId,
            @PathVariable UUID warrantyId) {
        return ApiResponse.success(expireAction.execute(workspaceId, warrantyId));
    }
}
