package com.company.scopery.modules.servicesupport.handover.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.handover.application.action.*;
import com.company.scopery.modules.servicesupport.handover.application.command.*;
import com.company.scopery.modules.servicesupport.handover.application.response.*;
import com.company.scopery.modules.servicesupport.handover.application.service.HandoverQueryService;
import com.company.scopery.modules.servicesupport.handover.http.request.*;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Handover Packages")
public class HandoverPackageController {
    private final HandoverQueryService query;
    private final CreateHandoverPackageAction createAction;
    private final FinalizeHandoverPackageAction finalizeAction;
    private final AddHandoverPackageItemAction addItemAction;

    public HandoverPackageController(HandoverQueryService query, CreateHandoverPackageAction createAction,
            FinalizeHandoverPackageAction finalizeAction, AddHandoverPackageItemAction addItemAction) {
        this.query = query; this.createAction = createAction;
        this.finalizeAction = finalizeAction; this.addItemAction = addItemAction;
    }

    @GetMapping(SupportApiPaths.HANDOVER_PACKAGES)
    @Operation(summary = "List handover packages")
    public ApiResponse<List<ServiceHandoverPackageResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listPackages(workspaceId));
    }

    @PostMapping(SupportApiPaths.HANDOVER_PACKAGES)
    @Operation(summary = "Create handover package")
    public ApiResponse<ServiceHandoverPackageResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateHandoverPackageRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateHandoverPackageCommand(req.projectId(), req.title())));
    }

    @PostMapping(SupportApiPaths.HANDOVER_PACKAGE_FINALIZE)
    @Operation(summary = "Finalize handover package")
    public ApiResponse<ServiceHandoverPackageResponse> finalize(@PathVariable UUID workspaceId,
            @PathVariable UUID packageId, @RequestParam(required = false) UUID finalizedBy) {
        return ApiResponse.success(finalizeAction.execute(workspaceId, packageId, finalizedBy));
    }

    @GetMapping(SupportApiPaths.HANDOVER_ITEMS)
    @Operation(summary = "List items in handover package")
    public ApiResponse<List<HandoverPackageItemResponse>> listItems(@PathVariable UUID workspaceId,
            @PathVariable UUID packageId) {
        return ApiResponse.success(query.listItems(workspaceId, packageId));
    }

    @PostMapping(SupportApiPaths.HANDOVER_ITEMS)
    @Operation(summary = "Add item to handover package")
    public ApiResponse<HandoverPackageItemResponse> addItem(@PathVariable UUID workspaceId,
            @PathVariable UUID packageId, @RequestBody @Valid AddHandoverItemRequest req) {
        return ApiResponse.success(addItemAction.execute(workspaceId, packageId,
                new AddHandoverItemCommand(req.itemType(), req.title())));
    }
}
