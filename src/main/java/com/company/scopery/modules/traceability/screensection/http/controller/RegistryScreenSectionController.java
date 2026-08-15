package com.company.scopery.modules.traceability.screensection.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screencomponent.application.action.BindComponentToSectionAction;
import com.company.scopery.modules.traceability.screencomponent.application.command.BindComponentToSectionCommand;
import com.company.scopery.modules.traceability.screencomponent.application.response.BindComponentToSectionResponse;
import com.company.scopery.modules.traceability.screencomponent.http.request.BindComponentToSectionRequest;
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
    private final BindComponentToSectionAction bindComponentToSection;
    public RegistryScreenSectionController(CreateRegistryScreenSectionAction create, UpdateRegistryScreenSectionAction update,
                                           DeleteRegistryScreenSectionAction delete, RegistryScreenSectionQueryService query,
                                           BindComponentToSectionAction bindComponentToSection) {
        this.create=create; this.update=update; this.delete=delete; this.query=query;
        this.bindComponentToSection=bindComponentToSection;
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
    @PostMapping("/{sectionId}/bind-component") @Operation(summary = "Bind component to section and import its fields as screen fields")
    public ApiResponse<BindComponentToSectionResponse> bindComponent(@PathVariable UUID workspaceId,
                                                                      @PathVariable UUID screenId,
                                                                      @PathVariable UUID sectionId,
                                                                      @Valid @RequestBody BindComponentToSectionRequest r) {
        return ApiResponse.success(bindComponentToSection.execute(new BindComponentToSectionCommand(
                workspaceId, screenId, sectionId, r.componentId(),
                r.displayOrder() != null ? r.displayOrder() : 0, r.note())));
    }
}
