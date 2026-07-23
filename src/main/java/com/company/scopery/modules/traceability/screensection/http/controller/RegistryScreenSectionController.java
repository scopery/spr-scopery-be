package com.company.scopery.modules.traceability.screensection.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screensection.application.action.CreateRegistryScreenSectionAction;
import com.company.scopery.modules.traceability.screensection.application.action.DeleteRegistryScreenSectionAction;
import com.company.scopery.modules.traceability.screensection.application.action.UpdateRegistryScreenSectionAction;
import com.company.scopery.modules.traceability.screensection.application.command.CreateRegistryScreenSectionCommand;
import com.company.scopery.modules.traceability.screensection.application.command.UpdateRegistryScreenSectionCommand;
import com.company.scopery.modules.traceability.screensection.application.response.RegistryScreenSectionResponse;
import com.company.scopery.modules.traceability.screensection.application.service.RegistryScreenSectionQueryService;
import com.company.scopery.modules.traceability.screensection.http.request.CreateRegistryScreenSectionRequest;
import com.company.scopery.modules.traceability.screensection.http.request.UpdateRegistryScreenSectionRequest;
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
    private final UpdateRegistryScreenSectionAction update;
    private final DeleteRegistryScreenSectionAction delete;
    private final RegistryScreenSectionQueryService query;
    public RegistryScreenSectionController(CreateRegistryScreenSectionAction create, UpdateRegistryScreenSectionAction update,
                                           DeleteRegistryScreenSectionAction delete, RegistryScreenSectionQueryService query) {
        this.create=create; this.update=update; this.delete=delete; this.query=query;
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
    @PutMapping("/{sectionId}") @Operation(summary = "Update screen section")
    public ApiResponse<RegistryScreenSectionResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID screenId,
                                                              @PathVariable UUID sectionId, @Valid @RequestBody UpdateRegistryScreenSectionRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryScreenSectionCommand(workspaceId, sectionId, r.name(), r.description(), r.displayOrder())));
    }
    @DeleteMapping("/{sectionId}") @Operation(summary = "Delete screen section")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId, @PathVariable UUID screenId, @PathVariable UUID sectionId) {
        delete.execute(workspaceId, sectionId);
        return ApiResponse.success(null);
    }
}
