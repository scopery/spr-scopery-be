package com.company.scopery.modules.raid.raiditem.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.raid.raidaction.application.action.CreateRaidActionAction;
import com.company.scopery.modules.raid.raidaction.application.command.CreateRaidActionCommand;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.application.service.RaidActionQueryService;
import com.company.scopery.modules.raid.raidaction.http.request.CreateRaidActionRequest;
import com.company.scopery.modules.raid.raidlink.application.action.CreateRaidLinkAction;
import com.company.scopery.modules.raid.raidlink.application.action.DeleteRaidLinkAction;
import com.company.scopery.modules.raid.raidlink.application.command.CreateRaidLinkCommand;
import com.company.scopery.modules.raid.raidlink.application.command.DeleteRaidLinkCommand;
import com.company.scopery.modules.raid.raidlink.application.response.RaidLinkResponse;
import com.company.scopery.modules.raid.raidlink.application.service.RaidLinkQueryService;
import com.company.scopery.modules.raid.raidlink.http.request.CreateRaidLinkRequest;
import com.company.scopery.modules.raid.raiditem.application.action.ArchiveRaidItemAction;
import com.company.scopery.modules.raid.raiditem.application.action.ChangeRaidItemStatusAction;
import com.company.scopery.modules.raid.raiditem.application.action.CloseRaidItemAction;
import com.company.scopery.modules.raid.raiditem.application.action.ConvertRiskToIssueAction;
import com.company.scopery.modules.raid.raiditem.application.action.CreateChangeRequestFromRaidAction;
import com.company.scopery.modules.raid.raiditem.application.action.CreateRaidItemAction;
import com.company.scopery.modules.raid.raiditem.application.action.EscalateRaidItemAction;
import com.company.scopery.modules.raid.raiditem.application.action.ReopenRaidItemAction;
import com.company.scopery.modules.raid.raiditem.application.action.ResolveRaidItemAction;
import com.company.scopery.modules.raid.raiditem.application.action.UpdateRaidItemAction;
import com.company.scopery.modules.raid.raiditem.application.command.*;
import com.company.scopery.modules.raid.raiditem.application.response.CreateChangeRequestFromRaidResponse;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.application.service.RaidItemQueryService;
import com.company.scopery.modules.raid.raiditem.http.request.*;
import com.company.scopery.modules.raid.shared.constant.RaidApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(RaidApiPaths.ITEMS)
@Tag(name = "RAID - Items")
public class RaidItemController {
    private final CreateRaidItemAction create;
    private final UpdateRaidItemAction update;
    private final ChangeRaidItemStatusAction changeStatus;
    private final ResolveRaidItemAction resolve;
    private final CloseRaidItemAction close;
    private final ReopenRaidItemAction reopen;
    private final EscalateRaidItemAction escalate;
    private final ArchiveRaidItemAction archive;
    private final ConvertRiskToIssueAction convert;
    private final CreateChangeRequestFromRaidAction createCr;
    private final CreateRaidActionAction createAction;
    private final CreateRaidLinkAction createLink;
    private final DeleteRaidLinkAction deleteLink;
    private final RaidItemQueryService query;
    private final RaidActionQueryService actionQuery;
    private final RaidLinkQueryService linkQuery;

    public RaidItemController(CreateRaidItemAction create, UpdateRaidItemAction update,
                              ChangeRaidItemStatusAction changeStatus, ResolveRaidItemAction resolve,
                              CloseRaidItemAction close, ReopenRaidItemAction reopen,
                              EscalateRaidItemAction escalate, ArchiveRaidItemAction archive,
                              ConvertRiskToIssueAction convert, CreateChangeRequestFromRaidAction createCr,
                              CreateRaidActionAction createAction, CreateRaidLinkAction createLink,
                              DeleteRaidLinkAction deleteLink, RaidItemQueryService query,
                              RaidActionQueryService actionQuery, RaidLinkQueryService linkQuery) {
        this.create = create;
        this.update = update;
        this.changeStatus = changeStatus;
        this.resolve = resolve;
        this.close = close;
        this.reopen = reopen;
        this.escalate = escalate;
        this.archive = archive;
        this.convert = convert;
        this.createCr = createCr;
        this.createAction = createAction;
        this.createLink = createLink;
        this.deleteLink = deleteLink;
        this.query = query;
        this.actionQuery = actionQuery;
        this.linkQuery = linkQuery;
    }

    @PostMapping
    @Operation(summary = "Create RAID item")
    public ApiResponse<RaidItemResponse> create(@PathVariable UUID projectId,
                                                @Valid @RequestBody CreateRaidItemRequest request) {
        return ApiResponse.success(create.execute(new CreateRaidItemCommand(
                projectId, request.type(), request.title(), request.code(), request.description(),
                request.ownerUserId(), request.probability(), request.impact(), request.riskResponseStrategy(),
                request.riskTrigger(), request.severity(), request.issueCategory(), request.impactSummary(),
                request.rootCause(), request.resolutionPlan(), request.assumptionStatement(),
                request.validationStatus(), request.dependencyType())));
    }

    @GetMapping
    @Operation(summary = "List RAID items")
    public ApiResponse<List<RaidItemResponse>> list(@PathVariable UUID projectId,
                                                    @RequestParam(required = false) String type) {
        return ApiResponse.success(query.list(projectId, type));
    }

    @GetMapping("/{raidItemId}")
    @Operation(summary = "Get RAID item")
    public ApiResponse<RaidItemResponse> get(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(query.get(projectId, raidItemId));
    }

    @PutMapping("/{raidItemId}")
    @Operation(summary = "Update RAID item")
    public ApiResponse<RaidItemResponse> update(@PathVariable UUID projectId, @PathVariable UUID raidItemId,
                                                @Valid @RequestBody UpdateRaidItemRequest request) {
        return ApiResponse.success(update.execute(new UpdateRaidItemCommand(
                projectId, raidItemId, request.title(), request.description(), request.ownerUserId(),
                request.probability(), request.impact(), request.riskResponseStrategy(), request.riskTrigger(),
                request.severity(), request.issueCategory(), request.impactSummary(), request.rootCause(),
                request.resolutionPlan(), request.assumptionStatement(), request.validationStatus(),
                request.dependencyType())));
    }

    @PatchMapping("/{raidItemId}/status")
    @Operation(summary = "Change RAID item status")
    public ApiResponse<RaidItemResponse> status(@PathVariable UUID projectId, @PathVariable UUID raidItemId,
                                                @Valid @RequestBody ChangeRaidItemStatusRequest request) {
        return ApiResponse.success(changeStatus.execute(
                new ChangeRaidItemStatusCommand(projectId, raidItemId, request.status())));
    }

    @PostMapping("/{raidItemId}/resolve")
    @Operation(summary = "Resolve RAID item")
    public ApiResponse<RaidItemResponse> resolve(@PathVariable UUID projectId, @PathVariable UUID raidItemId,
                                                 @Valid @RequestBody ResolveRaidItemRequest request) {
        return ApiResponse.success(resolve.execute(
                new ResolveRaidItemCommand(projectId, raidItemId, request.outcomeNote())));
    }

    @PostMapping("/{raidItemId}/close")
    @Operation(summary = "Close RAID item")
    public ApiResponse<RaidItemResponse> close(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(close.execute(new CloseRaidItemCommand(projectId, raidItemId)));
    }

    @PostMapping("/{raidItemId}/reopen")
    @Operation(summary = "Reopen RAID item")
    public ApiResponse<RaidItemResponse> reopen(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(reopen.execute(new ReopenRaidItemCommand(projectId, raidItemId)));
    }

    @PostMapping("/{raidItemId}/escalate")
    @Operation(summary = "Escalate RAID item")
    public ApiResponse<RaidItemResponse> escalate(@PathVariable UUID projectId, @PathVariable UUID raidItemId,
                                                  @Valid @RequestBody EscalateRaidItemRequest request) {
        return ApiResponse.success(escalate.execute(new EscalateRaidItemCommand(
                projectId, raidItemId, request.escalationLevel(), request.reason())));
    }

    @PatchMapping("/{raidItemId}/archive")
    @Operation(summary = "Archive RAID item")
    public ApiResponse<RaidItemResponse> archive(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(archive.execute(new ArchiveRaidItemCommand(projectId, raidItemId)));
    }

    @PostMapping("/{raidItemId}/convert-risk-to-issue")
    @Operation(summary = "Convert risk to issue")
    public ApiResponse<RaidItemResponse> convert(@PathVariable UUID projectId, @PathVariable UUID raidItemId) {
        return ApiResponse.success(convert.execute(projectId, raidItemId));
    }

    @PostMapping("/{raidItemId}/create-change-request-draft")
    @Operation(summary = "Create CR draft from RAID")
    public ApiResponse<CreateChangeRequestFromRaidResponse> createCr(@PathVariable UUID projectId,
                                                                     @PathVariable UUID raidItemId) {
        return ApiResponse.success(createCr.execute(projectId, raidItemId));
    }

    @PostMapping("/{raidItemId}/actions")
    @Operation(summary = "Create RAID action")
    public ApiResponse<RaidActionResponse> createAction(@PathVariable UUID projectId,
                                                        @PathVariable UUID raidItemId,
                                                        @Valid @RequestBody CreateRaidActionRequest request) {
        return ApiResponse.success(createAction.execute(new CreateRaidActionCommand(
                projectId, raidItemId, request.title(), request.description(),
                request.ownerUserId(), request.dueDate())));
    }

    @GetMapping("/{raidItemId}/actions")
    @Operation(summary = "List RAID actions for item")
    public ApiResponse<List<RaidActionResponse>> listActions(@PathVariable UUID projectId,
                                                             @PathVariable UUID raidItemId) {
        return ApiResponse.success(actionQuery.listByItem(projectId, raidItemId));
    }

    @PostMapping("/{raidItemId}/links")
    @Operation(summary = "Create RAID link")
    public ApiResponse<RaidLinkResponse> createLink(@PathVariable UUID projectId,
                                                    @PathVariable UUID raidItemId,
                                                    @Valid @RequestBody CreateRaidLinkRequest request) {
        return ApiResponse.success(createLink.execute(new CreateRaidLinkCommand(
                projectId, raidItemId, request.linkType(), request.targetType(), request.targetId())));
    }

    @GetMapping("/{raidItemId}/links")
    @Operation(summary = "List RAID links for item")
    public ApiResponse<List<RaidLinkResponse>> listLinks(@PathVariable UUID projectId,
                                                         @PathVariable UUID raidItemId) {
        return ApiResponse.success(linkQuery.list(projectId, raidItemId));
    }

    @DeleteMapping("/{raidItemId}/links/{linkId}")
    @Operation(summary = "Delete RAID link")
    public ApiResponse<Void> deleteLink(@PathVariable UUID projectId,
                                        @PathVariable UUID raidItemId,
                                        @PathVariable UUID linkId) {
        deleteLink.execute(new DeleteRaidLinkCommand(projectId, raidItemId, linkId));
        return ApiResponse.success(null);
    }
}
