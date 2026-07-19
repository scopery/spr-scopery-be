package com.company.scopery.modules.scope.deliverable.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.scope.deliverable.application.action.*;
import com.company.scopery.modules.scope.deliverable.application.command.*;
import com.company.scopery.modules.scope.deliverable.application.response.DeliverableResponse;
import com.company.scopery.modules.scope.deliverable.application.service.DeliverableQueryService;
import com.company.scopery.modules.scope.deliverable.http.request.*;
import com.company.scopery.modules.scope.shared.constant.ScopeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ScopeApiPaths.DELIVERABLES)
@Tag(name = "Scope - Deliverables")
public class DeliverableController {
    private final CreateDeliverableAction create;
    private final UpdateDeliverableAction update;
    private final ArchiveDeliverableAction archive;
    private final ChangeDeliverableStatusAction changeStatus;
    private final AcceptDeliverableAction accept;
    private final ReopenDeliverableAction reopen;
    private final DeliverableQueryService query;

    public DeliverableController(CreateDeliverableAction create, UpdateDeliverableAction update,
                                 ArchiveDeliverableAction archive, ChangeDeliverableStatusAction changeStatus,
                                 AcceptDeliverableAction accept, ReopenDeliverableAction reopen,
                                 DeliverableQueryService query) {
        this.create = create;
        this.update = update;
        this.archive = archive;
        this.changeStatus = changeStatus;
        this.accept = accept;
        this.reopen = reopen;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create deliverable")
    public ApiResponse<DeliverableResponse> create(@PathVariable UUID projectId,
                                                   @Valid @RequestBody CreateDeliverableRequest request) {
        return ApiResponse.success(create.execute(new CreateDeliverableCommand(
                projectId, request.type(), request.code(), request.title(),
                request.description(), request.acceptanceRequired())));
    }

    @GetMapping
    @Operation(summary = "List deliverables")
    public ApiResponse<List<DeliverableResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{deliverableId}")
    @Operation(summary = "Get deliverable")
    public ApiResponse<DeliverableResponse> get(@PathVariable UUID projectId, @PathVariable UUID deliverableId) {
        return ApiResponse.success(query.get(projectId, deliverableId));
    }

    @PutMapping("/{deliverableId}")
    @Operation(summary = "Update deliverable")
    public ApiResponse<DeliverableResponse> update(@PathVariable UUID projectId, @PathVariable UUID deliverableId,
                                                   @Valid @RequestBody UpdateDeliverableRequest request) {
        return ApiResponse.success(update.execute(new UpdateDeliverableCommand(
                projectId, deliverableId, request.type(), request.title(), request.description())));
    }

    @PatchMapping("/{deliverableId}/status")
    @Operation(summary = "Change deliverable status")
    public ApiResponse<DeliverableResponse> changeStatus(@PathVariable UUID projectId, @PathVariable UUID deliverableId,
                                                         @Valid @RequestBody ChangeDeliverableStatusRequest request) {
        return ApiResponse.success(changeStatus.execute(new ChangeDeliverableStatusCommand(
                projectId, deliverableId, request.status())));
    }

    @PatchMapping("/{deliverableId}/archive")
    @Operation(summary = "Archive deliverable")
    public ApiResponse<DeliverableResponse> archive(@PathVariable UUID projectId, @PathVariable UUID deliverableId) {
        return ApiResponse.success(archive.execute(new ArchiveDeliverableCommand(projectId, deliverableId)));
    }

    @PostMapping("/{deliverableId}/accept")
    @Operation(summary = "Formally accept deliverable")
    public ApiResponse<DeliverableResponse> accept(@PathVariable UUID projectId, @PathVariable UUID deliverableId) {
        return ApiResponse.success(accept.execute(new AcceptDeliverableCommand(projectId, deliverableId)));
    }

    @PostMapping("/{deliverableId}/reopen")
    @Operation(summary = "Reopen accepted deliverable")
    public ApiResponse<DeliverableResponse> reopen(@PathVariable UUID projectId,
                                                   @PathVariable UUID deliverableId,
                                                   @Valid @RequestBody ReopenDeliverableRequest request) {
        return ApiResponse.success(reopen.execute(new ReopenDeliverableCommand(
                projectId, deliverableId, request.reason())));
    }
}
