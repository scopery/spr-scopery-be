package com.company.scopery.modules.traceability.screensection.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screensection.application.action.CreateRegistryScreenSectionAction;
import com.company.scopery.modules.traceability.screensection.application.command.CreateRegistryScreenSectionCommand;
import com.company.scopery.modules.traceability.screensection.application.response.RegistryScreenSectionResponse;
import com.company.scopery.modules.traceability.screensection.application.service.RegistryScreenSectionQueryService;
import com.company.scopery.modules.traceability.screensection.http.request.CreateRegistryScreenSectionRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_SECTIONS)
@Tag(name = "Traceability - Screen Sections")
public class RegistryScreenSectionController {
    private final CreateRegistryScreenSectionAction create;
    private final RegistryScreenSectionQueryService query;
    public RegistryScreenSectionController(CreateRegistryScreenSectionAction create, RegistryScreenSectionQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create screen section")
    public ApiResponse<RegistryScreenSectionResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID screenId,
                                                              @Valid @RequestBody CreateRegistryScreenSectionRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryScreenSectionCommand(screenId, workspaceId, r.name(), r.description(), r.displayOrder())));
    }
    @GetMapping @Operation(summary = "List screen sections")
    public ApiResponse<List<RegistryScreenSectionResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID screenId) {
        return ApiResponse.success(query.list(workspaceId, screenId));
    }
    @GetMapping("/{sectionId}") @Operation(summary = "Get screen section")
    public ApiResponse<RegistryScreenSectionResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID sectionId) {
        return ApiResponse.success(query.get(workspaceId, sectionId));
    }
}
