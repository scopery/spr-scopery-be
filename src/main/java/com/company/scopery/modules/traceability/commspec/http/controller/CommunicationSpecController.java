package com.company.scopery.modules.traceability.commspec.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.commspec.application.action.ArchiveCommunicationSpecAction;
import com.company.scopery.modules.traceability.commspec.application.action.CreateCommunicationSpecAction;
import com.company.scopery.modules.traceability.commspec.application.action.MarkCommunicationSpecReadyAction;
import com.company.scopery.modules.traceability.commspec.application.action.UpdateCommunicationSpecAction;
import com.company.scopery.modules.traceability.commspec.application.command.CreateCommunicationSpecCommand;
import com.company.scopery.modules.traceability.commspec.application.command.UpdateCommunicationSpecCommand;
import com.company.scopery.modules.traceability.commspec.application.response.CommunicationSpecResponse;
import com.company.scopery.modules.traceability.commspec.application.service.CommunicationSpecQueryService;
import com.company.scopery.modules.traceability.commspec.http.request.CreateCommunicationSpecRequest;
import com.company.scopery.modules.traceability.commspec.http.request.UpdateCommunicationSpecRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.COMMUNICATION_SPECS)
@Tag(name = "Traceability - Communication Specifications")
public class CommunicationSpecController {

    private final CreateCommunicationSpecAction create;
    private final UpdateCommunicationSpecAction update;
    private final MarkCommunicationSpecReadyAction markReady;
    private final ArchiveCommunicationSpecAction archive;
    private final CommunicationSpecQueryService query;

    public CommunicationSpecController(CreateCommunicationSpecAction create,
                                       UpdateCommunicationSpecAction update,
                                       MarkCommunicationSpecReadyAction markReady,
                                       ArchiveCommunicationSpecAction archive,
                                       CommunicationSpecQueryService query) {
        this.create = create;
        this.update = update;
        this.markReady = markReady;
        this.archive = archive;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create communication specification")
    public ApiResponse<CommunicationSpecResponse> create(
            @PathVariable UUID workspaceId,
            @PathVariable UUID applicationId,
            @Valid @RequestBody CreateCommunicationSpecRequest r) {
        return ApiResponse.success(create.execute(new CreateCommunicationSpecCommand(
                workspaceId, applicationId, r.code(), r.name(), r.description(),
                r.triggerName(), r.triggerKey(), r.triggerTiming(),
                r.conditionJson(), r.suppressionConditionJson(), r.deliveryPolicyJson(),
                r.inAppContractJson(), r.emailContractJson(), r.recipientsJson(), r.ownerId())));
    }

    @GetMapping
    @Operation(summary = "List communication specifications")
    public ApiResponse<List<CommunicationSpecResponse>> list(
            @PathVariable UUID workspaceId,
            @PathVariable UUID applicationId) {
        return ApiResponse.success(query.list(workspaceId, applicationId));
    }

    @GetMapping("/{communicationSpecId}")
    @Operation(summary = "Get communication specification")
    public ApiResponse<CommunicationSpecResponse> get(
            @PathVariable UUID workspaceId,
            @PathVariable UUID applicationId,
            @PathVariable UUID communicationSpecId) {
        return ApiResponse.success(query.get(workspaceId, applicationId, communicationSpecId));
    }

    @PutMapping("/{communicationSpecId}")
    @Operation(summary = "Update communication specification")
    public ApiResponse<CommunicationSpecResponse> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID applicationId,
            @PathVariable UUID communicationSpecId,
            @Valid @RequestBody UpdateCommunicationSpecRequest r) {
        return ApiResponse.success(update.execute(new UpdateCommunicationSpecCommand(
                workspaceId, applicationId, communicationSpecId,
                r.name(), r.description(),
                r.triggerName(), r.triggerKey(), r.triggerTiming(),
                r.conditionJson(), r.suppressionConditionJson(), r.deliveryPolicyJson(),
                r.inAppContractJson(), r.emailContractJson(), r.recipientsJson(), r.ownerId())));
    }

    @PostMapping("/{communicationSpecId}/mark-ready")
    @Operation(summary = "Mark communication specification READY")
    public ApiResponse<CommunicationSpecResponse> markReady(
            @PathVariable UUID workspaceId,
            @PathVariable UUID applicationId,
            @PathVariable UUID communicationSpecId) {
        return ApiResponse.success(markReady.execute(workspaceId, applicationId, communicationSpecId));
    }

    @DeleteMapping("/{communicationSpecId}")
    @Operation(summary = "Archive communication specification")
    public ApiResponse<Void> archive(
            @PathVariable UUID workspaceId,
            @PathVariable UUID applicationId,
            @PathVariable UUID communicationSpecId) {
        archive.execute(workspaceId, applicationId, communicationSpecId);
        return ApiResponse.success(null);
    }
}
