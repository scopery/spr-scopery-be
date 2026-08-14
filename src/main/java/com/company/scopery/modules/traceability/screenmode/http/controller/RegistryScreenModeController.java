package com.company.scopery.modules.traceability.screenmode.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screenmode.application.action.CreateRegistryScreenModeAction;
import com.company.scopery.modules.traceability.screenmode.application.action.DeleteRegistryScreenModeAction;
import com.company.scopery.modules.traceability.screenmode.application.action.UpdateRegistryScreenModeAction;
import com.company.scopery.modules.traceability.screenmode.application.command.CreateRegistryScreenModeCommand;
import com.company.scopery.modules.traceability.screenmode.application.command.UpdateRegistryScreenModeCommand;
import com.company.scopery.modules.traceability.screenmode.application.response.RegistryScreenModeResponse;
import com.company.scopery.modules.traceability.screenmode.application.service.RegistryScreenModeQueryService;
import com.company.scopery.modules.traceability.screenmode.http.request.CreateRegistryScreenModeRequest;
import com.company.scopery.modules.traceability.screenmode.http.request.UpdateRegistryScreenModeRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_MODES)
@Tag(name = "Traceability - Screen Modes")
public class RegistryScreenModeController {

    private final CreateRegistryScreenModeAction create;
    private final UpdateRegistryScreenModeAction update;
    private final DeleteRegistryScreenModeAction delete;
    private final RegistryScreenModeQueryService query;

    public RegistryScreenModeController(CreateRegistryScreenModeAction create,
                                        UpdateRegistryScreenModeAction update,
                                        DeleteRegistryScreenModeAction delete,
                                        RegistryScreenModeQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create screen mode")
    public ApiResponse<RegistryScreenModeResponse> create(@PathVariable UUID workspaceId,
                                                          @PathVariable UUID screenId,
                                                          @Valid @RequestBody CreateRegistryScreenModeRequest r) {
        return ApiResponse.success(create.execute(
                new CreateRegistryScreenModeCommand(screenId, workspaceId, r.modeCode(), r.name(), r.displayOrder())));
    }

    @GetMapping
    @Operation(summary = "List screen modes")
    public ApiResponse<List<RegistryScreenModeResponse>> list(@PathVariable UUID workspaceId,
                                                              @PathVariable UUID screenId) {
        return ApiResponse.success(query.list(workspaceId, screenId));
    }

    @GetMapping("/{modeId}")
    @Operation(summary = "Get screen mode")
    public ApiResponse<RegistryScreenModeResponse> get(@PathVariable UUID workspaceId,
                                                       @PathVariable UUID modeId) {
        return ApiResponse.success(query.get(workspaceId, modeId));
    }

    @PutMapping("/{modeId}")
    @Operation(summary = "Update screen mode")
    public ApiResponse<RegistryScreenModeResponse> update(@PathVariable UUID workspaceId,
                                                          @PathVariable UUID screenId,
                                                          @PathVariable UUID modeId,
                                                          @Valid @RequestBody UpdateRegistryScreenModeRequest r) {
        return ApiResponse.success(update.execute(
                new UpdateRegistryScreenModeCommand(workspaceId, modeId, r.name(), r.displayOrder())));
    }

    @DeleteMapping("/{modeId}")
    @Operation(summary = "Delete screen mode")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId,
                                    @PathVariable UUID screenId,
                                    @PathVariable UUID modeId) {
        delete.execute(workspaceId, modeId);
        return ApiResponse.success(null);
    }
}
