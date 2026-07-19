package com.company.scopery.modules.traceability.screen.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screen.application.action.CreateRegistryScreenAction;
import com.company.scopery.modules.traceability.screen.application.command.CreateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.response.RegistryScreenResponse;
import com.company.scopery.modules.traceability.screen.application.service.RegistryScreenQueryService;
import com.company.scopery.modules.traceability.screen.http.request.CreateRegistryScreenRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.SCREENS)
@Tag(name = "Traceability - Screens")
public class RegistryScreenController {
    private final CreateRegistryScreenAction create;
    private final RegistryScreenQueryService query;
    public RegistryScreenController(CreateRegistryScreenAction create, RegistryScreenQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create screen")
    public ApiResponse<RegistryScreenResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID applicationId, @Valid @RequestBody CreateRegistryScreenRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryScreenCommand(workspaceId, applicationId, r.projectId(), r.code(), r.name(), r.routePath())));
    }
    @GetMapping @Operation(summary = "List screens")
    public ApiResponse<List<RegistryScreenResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID applicationId) {
        return ApiResponse.success(query.list(workspaceId, applicationId));
    }
    @GetMapping("/{screenId}") @Operation(summary = "Get screen")
    public ApiResponse<RegistryScreenResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID applicationId, @PathVariable UUID screenId) {
        return ApiResponse.success(query.get(workspaceId, applicationId, screenId));
    }
}
