package com.company.scopery.modules.traceability.specdocrevision.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.specdocrevision.application.action.CreateRegistrySpecDocRevisionAction;
import com.company.scopery.modules.traceability.specdocrevision.application.action.DeleteRegistrySpecDocRevisionAction;
import com.company.scopery.modules.traceability.specdocrevision.application.action.UpdateRegistrySpecDocRevisionAction;
import com.company.scopery.modules.traceability.specdocrevision.application.command.CreateRegistrySpecDocRevisionCommand;
import com.company.scopery.modules.traceability.specdocrevision.application.command.DeleteRegistrySpecDocRevisionCommand;
import com.company.scopery.modules.traceability.specdocrevision.application.command.UpdateRegistrySpecDocRevisionCommand;
import com.company.scopery.modules.traceability.specdocrevision.application.response.RegistrySpecDocRevisionResponse;
import com.company.scopery.modules.traceability.specdocrevision.application.service.RegistrySpecDocRevisionQueryService;
import com.company.scopery.modules.traceability.specdocrevision.http.request.CreateRegistrySpecDocRevisionRequest;
import com.company.scopery.modules.traceability.specdocrevision.http.request.UpdateRegistrySpecDocRevisionRequest;
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
@RequestMapping(TraceabilityApiPaths.SPEC_DOC_REVISIONS)
@Tag(name = "Traceability - Screen Spec Doc")
public class RegistrySpecDocRevisionController {

    private final CreateRegistrySpecDocRevisionAction create;
    private final UpdateRegistrySpecDocRevisionAction update;
    private final DeleteRegistrySpecDocRevisionAction delete;
    private final RegistrySpecDocRevisionQueryService query;

    public RegistrySpecDocRevisionController(CreateRegistrySpecDocRevisionAction create,
                                             UpdateRegistrySpecDocRevisionAction update,
                                             DeleteRegistrySpecDocRevisionAction delete,
                                             RegistrySpecDocRevisionQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create spec doc revision")
    public ApiResponse<RegistrySpecDocRevisionResponse> create(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId,
            @Valid @RequestBody CreateRegistrySpecDocRevisionRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistrySpecDocRevisionCommand(
                workspaceId,
                documentId,
                r.revisionNo(),
                r.targetSheetName(),
                r.details(),
                r.personInCharge(),
                r.color(),
                r.changedAt(),
                r.displayOrder())));
    }

    @GetMapping
    @Operation(summary = "List spec doc revisions")
    public ApiResponse<List<RegistrySpecDocRevisionResponse>> list(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId) {
        return ApiResponse.success(query.list(workspaceId, documentId));
    }

    @PutMapping("/{revisionId}")
    @Operation(summary = "Update spec doc revision")
    public ApiResponse<RegistrySpecDocRevisionResponse> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId,
            @PathVariable UUID revisionId,
            @Valid @RequestBody UpdateRegistrySpecDocRevisionRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistrySpecDocRevisionCommand(
                workspaceId,
                revisionId,
                r.revisionNo(),
                r.targetSheetName(),
                r.details(),
                r.personInCharge(),
                r.color(),
                r.changedAt(),
                r.displayOrder())));
    }

    @DeleteMapping("/{revisionId}")
    @Operation(summary = "Delete spec doc revision")
    public ApiResponse<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID documentId,
            @PathVariable UUID revisionId) {
        delete.execute(new DeleteRegistrySpecDocRevisionCommand(workspaceId, revisionId));
        return ApiResponse.success(null);
    }
}
