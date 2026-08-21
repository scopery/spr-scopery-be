package com.company.scopery.modules.traceability.application.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.application.application.action.CreateRegistryApplicationAction;
import com.company.scopery.modules.traceability.application.application.action.UpdateRegistryApplicationAction;
import com.company.scopery.modules.traceability.application.application.command.CreateRegistryApplicationCommand;
import com.company.scopery.modules.traceability.application.application.command.UpdateRegistryApplicationCommand;
import com.company.scopery.modules.traceability.application.application.response.RegistryApplicationResponse;
import com.company.scopery.modules.traceability.application.application.service.RegistryApplicationQueryService;
import com.company.scopery.modules.traceability.application.http.request.CreateRegistryApplicationRequest;
import com.company.scopery.modules.traceability.application.http.request.UpdateRegistryApplicationRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.APPLICATIONS)
@Tag(name = "Traceability - Applications")
public class RegistryApplicationController {

    private final CreateRegistryApplicationAction create;
    private final UpdateRegistryApplicationAction update;
    private final RegistryApplicationQueryService query;

    public RegistryApplicationController(CreateRegistryApplicationAction create,
                                          UpdateRegistryApplicationAction update,
                                          RegistryApplicationQueryService query) {
        this.create = create;
        this.update = update;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create application")
    public ApiResponse<RegistryApplicationResponse> create(@PathVariable UUID workspaceId,
                                                            @Valid @RequestBody CreateRegistryApplicationRequest r) {
        return ApiResponse.success(create.execute(
                new CreateRegistryApplicationCommand(workspaceId, r.code(), r.name(), r.description(), r.ownerUserId())));
    }

    @PutMapping("/{applicationId}")
    @Operation(summary = "Update application name and description")
    public ApiResponse<RegistryApplicationResponse> update(@PathVariable UUID workspaceId,
                                                            @PathVariable UUID applicationId,
                                                            @Valid @RequestBody UpdateRegistryApplicationRequest r) {
        return ApiResponse.success(update.execute(
                new UpdateRegistryApplicationCommand(workspaceId, applicationId, r.name(), r.description())));
    }

    @GetMapping
    @Operation(summary = "List applications")
    public ApiResponse<List<RegistryApplicationResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.list(workspaceId));
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "Get application")
    public ApiResponse<RegistryApplicationResponse> get(@PathVariable UUID workspaceId,
                                                         @PathVariable UUID applicationId) {
        return ApiResponse.success(query.get(workspaceId, applicationId));
    }
}
