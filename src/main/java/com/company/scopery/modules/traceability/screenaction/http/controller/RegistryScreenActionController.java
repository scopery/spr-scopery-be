package com.company.scopery.modules.traceability.screenaction.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screenaction.application.action.CreateRegistryScreenActionAction;
import com.company.scopery.modules.traceability.screenaction.application.command.CreateRegistryScreenActionCommand;
import com.company.scopery.modules.traceability.screenaction.application.response.RegistryScreenActionResponse;
import com.company.scopery.modules.traceability.screenaction.application.service.RegistryScreenActionQueryService;
import com.company.scopery.modules.traceability.screenaction.http.request.CreateRegistryScreenActionRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_ACTIONS)
@Tag(name = "Traceability - Screen Actions")
public class RegistryScreenActionController {
    private final CreateRegistryScreenActionAction create;
    private final RegistryScreenActionQueryService query;
    public RegistryScreenActionController(CreateRegistryScreenActionAction create, RegistryScreenActionQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create screen action")
    public ApiResponse<RegistryScreenActionResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID screenId,
                                                             @Valid @RequestBody CreateRegistryScreenActionRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryScreenActionCommand(screenId, workspaceId,
                r.actionCode(), r.name(), r.actionType(), r.description(), r.displayOrder())));
    }
    @GetMapping @Operation(summary = "List screen actions")
    public ApiResponse<List<RegistryScreenActionResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID screenId) {
        return ApiResponse.success(query.list(workspaceId, screenId));
    }
    @GetMapping("/{actionId}") @Operation(summary = "Get screen action")
    public ApiResponse<RegistryScreenActionResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID actionId) {
        return ApiResponse.success(query.get(workspaceId, actionId));
    }
}
