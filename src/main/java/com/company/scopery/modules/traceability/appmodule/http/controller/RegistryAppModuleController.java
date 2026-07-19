package com.company.scopery.modules.traceability.appmodule.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.appmodule.application.action.CreateRegistryAppModuleAction;
import com.company.scopery.modules.traceability.appmodule.application.command.CreateRegistryAppModuleCommand;
import com.company.scopery.modules.traceability.appmodule.application.response.RegistryAppModuleResponse;
import com.company.scopery.modules.traceability.appmodule.application.service.RegistryAppModuleQueryService;
import com.company.scopery.modules.traceability.appmodule.http.request.CreateRegistryAppModuleRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.APP_MODULES)
@Tag(name = "Traceability - Application Modules")
public class RegistryAppModuleController {
    private final CreateRegistryAppModuleAction create;
    private final RegistryAppModuleQueryService query;
    public RegistryAppModuleController(CreateRegistryAppModuleAction create, RegistryAppModuleQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create application module")
    public ApiResponse<RegistryAppModuleResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                          @Valid @RequestBody CreateRegistryAppModuleRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryAppModuleCommand(applicationId, workspaceId, r.code(), r.name(), r.description())));
    }
    @GetMapping @Operation(summary = "List application modules")
    public ApiResponse<List<RegistryAppModuleResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID applicationId) {
        return ApiResponse.success(query.list(applicationId));
    }
    @GetMapping("/{appModuleId}") @Operation(summary = "Get application module")
    public ApiResponse<RegistryAppModuleResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                       @PathVariable UUID appModuleId) {
        return ApiResponse.success(query.get(workspaceId, appModuleId));
    }
}
