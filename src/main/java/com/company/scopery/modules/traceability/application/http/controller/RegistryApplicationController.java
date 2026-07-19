package com.company.scopery.modules.traceability.application.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.application.application.action.CreateRegistryApplicationAction;
import com.company.scopery.modules.traceability.application.application.command.CreateRegistryApplicationCommand;
import com.company.scopery.modules.traceability.application.application.response.RegistryApplicationResponse;
import com.company.scopery.modules.traceability.application.application.service.RegistryApplicationQueryService;
import com.company.scopery.modules.traceability.application.http.request.CreateRegistryApplicationRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.APPLICATIONS)
@Tag(name = "Traceability - Applications")
public class RegistryApplicationController {
    private final CreateRegistryApplicationAction create;
    private final RegistryApplicationQueryService query;
    public RegistryApplicationController(CreateRegistryApplicationAction create, RegistryApplicationQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create application")
    public ApiResponse<RegistryApplicationResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateRegistryApplicationRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryApplicationCommand(workspaceId, r.code(), r.name(), r.description(), r.ownerUserId())));
    }
    @GetMapping @Operation(summary = "List applications")
    public ApiResponse<List<RegistryApplicationResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @GetMapping("/{applicationId}") @Operation(summary = "Get application")
    public ApiResponse<RegistryApplicationResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID applicationId) {
        return ApiResponse.success(query.get(workspaceId, applicationId));
    }
}
