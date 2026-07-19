package com.company.scopery.modules.traceability.screenfield.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screenfield.application.action.CreateRegistryScreenFieldAction;
import com.company.scopery.modules.traceability.screenfield.application.command.CreateRegistryScreenFieldCommand;
import com.company.scopery.modules.traceability.screenfield.application.response.RegistryScreenFieldResponse;
import com.company.scopery.modules.traceability.screenfield.application.service.RegistryScreenFieldQueryService;
import com.company.scopery.modules.traceability.screenfield.http.request.CreateRegistryScreenFieldRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_FIELDS)
@Tag(name = "Traceability - Screen Fields")
public class RegistryScreenFieldController {
    private final CreateRegistryScreenFieldAction create;
    private final RegistryScreenFieldQueryService query;
    public RegistryScreenFieldController(CreateRegistryScreenFieldAction create, RegistryScreenFieldQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create screen field")
    public ApiResponse<RegistryScreenFieldResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID screenId,
                                                            @Valid @RequestBody CreateRegistryScreenFieldRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryScreenFieldCommand(screenId, r.sectionId(), workspaceId,
                r.fieldKey(), r.label(), r.fieldType(), r.description(), r.required(), r.displayOrder())));
    }
    @GetMapping @Operation(summary = "List screen fields")
    public ApiResponse<List<RegistryScreenFieldResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID screenId) {
        return ApiResponse.success(query.list(workspaceId, screenId));
    }
    @GetMapping("/{fieldId}") @Operation(summary = "Get screen field")
    public ApiResponse<RegistryScreenFieldResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID fieldId) {
        return ApiResponse.success(query.get(workspaceId, fieldId));
    }
}
