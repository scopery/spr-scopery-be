package com.company.scopery.modules.traceability.screenspecdoc.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screenspecdoc.application.action.AddScreenToSpecDocAction;
import com.company.scopery.modules.traceability.screenspecdoc.application.action.CreateRegistryScreenSpecDocAction;
import com.company.scopery.modules.traceability.screenspecdoc.application.action.DeleteRegistryScreenSpecDocAction;
import com.company.scopery.modules.traceability.screenspecdoc.application.action.RemoveScreenFromSpecDocAction;
import com.company.scopery.modules.traceability.screenspecdoc.application.action.UpdateRegistryScreenSpecDocAction;
import com.company.scopery.modules.traceability.screenspecdoc.application.command.AddScreenToSpecDocCommand;
import com.company.scopery.modules.traceability.screenspecdoc.application.command.CreateRegistryScreenSpecDocCommand;
import com.company.scopery.modules.traceability.screenspecdoc.application.command.DeleteRegistryScreenSpecDocCommand;
import com.company.scopery.modules.traceability.screenspecdoc.application.command.RemoveScreenFromSpecDocCommand;
import com.company.scopery.modules.traceability.screenspecdoc.application.command.UpdateRegistryScreenSpecDocCommand;
import com.company.scopery.modules.traceability.screenspecdoc.application.response.RegistryScreenSpecDocResponse;
import com.company.scopery.modules.traceability.screenspecdoc.application.response.RegistryScreenSpecDocWithScreensResponse;
import com.company.scopery.modules.traceability.screenspecdoc.application.response.SpecDocFullSpecResponse;
import com.company.scopery.modules.traceability.screenspecdoc.application.response.SpecDocScreenResponse;
import com.company.scopery.modules.traceability.screenspecdoc.application.service.RegistryScreenSpecDocQueryService;
import com.company.scopery.modules.traceability.screenspecdoc.http.request.AddScreenToSpecDocRequest;
import com.company.scopery.modules.traceability.screenspecdoc.http.request.CreateRegistryScreenSpecDocRequest;
import com.company.scopery.modules.traceability.screenspecdoc.http.request.UpdateRegistryScreenSpecDocRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_SPEC_DOCS)
@Tag(name = "Traceability - Screen Spec Doc")
public class RegistryScreenSpecDocController {

    private final CreateRegistryScreenSpecDocAction create;
    private final UpdateRegistryScreenSpecDocAction update;
    private final DeleteRegistryScreenSpecDocAction delete;
    private final AddScreenToSpecDocAction addScreen;
    private final RemoveScreenFromSpecDocAction removeScreen;
    private final RegistryScreenSpecDocQueryService query;

    public RegistryScreenSpecDocController(CreateRegistryScreenSpecDocAction create,
                                           UpdateRegistryScreenSpecDocAction update,
                                           DeleteRegistryScreenSpecDocAction delete,
                                           AddScreenToSpecDocAction addScreen,
                                           RemoveScreenFromSpecDocAction removeScreen,
                                           RegistryScreenSpecDocQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.addScreen = addScreen;
        this.removeScreen = removeScreen;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create screen spec document")
    public ApiResponse<RegistryScreenSpecDocResponse> create(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateRegistryScreenSpecDocRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryScreenSpecDocCommand(
                workspaceId,
                r.projectId(),
                r.documentCode(),
                r.documentName(),
                r.projectName(),
                r.systemName(),
                r.phaseName(),
                r.language(),
                r.overview(),
                r.figmaUrl())));
    }

    @GetMapping
    @Operation(summary = "List screen spec documents by project")
    public ApiResponse<List<RegistryScreenSpecDocResponse>> list(
            @PathVariable UUID workspaceId,
            @RequestParam UUID projectId) {
        return ApiResponse.success(query.list(workspaceId, projectId));
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "Get screen spec document with screens")
    public ApiResponse<RegistryScreenSpecDocWithScreensResponse> get(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId) {
        return ApiResponse.success(query.get(workspaceId, null, documentId));
    }

    @PutMapping("/{documentId}")
    @Operation(summary = "Update screen spec document")
    public ApiResponse<RegistryScreenSpecDocResponse> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId,
            @Valid @RequestBody UpdateRegistryScreenSpecDocRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryScreenSpecDocCommand(
                workspaceId,
                documentId,
                r.documentName(),
                r.projectName(),
                r.systemName(),
                r.phaseName(),
                r.language(),
                r.overview(),
                r.figmaUrl())));
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete screen spec document")
    public ApiResponse<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId) {
        delete.execute(new DeleteRegistryScreenSpecDocCommand(workspaceId, documentId));
        return ApiResponse.success(null);
    }

    @PostMapping("/{documentId}/screens")
    @Operation(summary = "Add screen to spec document")
    public ApiResponse<SpecDocScreenResponse> addScreen(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId,
            @Valid @RequestBody AddScreenToSpecDocRequest r) {
        return ApiResponse.success(addScreen.execute(new AddScreenToSpecDocCommand(
                workspaceId, documentId, r.screenId(), r.displayOrder(), r.note())));
    }

    @DeleteMapping("/{documentId}/screens/{screenId}")
    @Operation(summary = "Remove screen from spec document")
    public ApiResponse<Void> removeScreen(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId,
            @PathVariable UUID screenId) {
        removeScreen.execute(new RemoveScreenFromSpecDocCommand(workspaceId, documentId, screenId));
        return ApiResponse.success(null);
    }

    @GetMapping("/{documentId}/full-spec")
    @Operation(summary = "Get full spec for a document — includes doc metadata, change history, and full screen spec for each linked screen")
    public ApiResponse<SpecDocFullSpecResponse> getFullSpec(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId) {
        return ApiResponse.success(query.getFullSpec(workspaceId, documentId));
    }
}
