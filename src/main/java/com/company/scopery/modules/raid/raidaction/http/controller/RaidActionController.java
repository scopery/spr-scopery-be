package com.company.scopery.modules.raid.raidaction.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.raid.raidaction.application.action.CancelRaidActionAction;
import com.company.scopery.modules.raid.raidaction.application.action.CompleteRaidActionAction;
import com.company.scopery.modules.raid.raidaction.application.action.CreateLinkedTaskFromRaidActionAction;
import com.company.scopery.modules.raid.raidaction.application.action.UpdateRaidActionAction;
import com.company.scopery.modules.raid.raidaction.application.command.CancelRaidActionCommand;
import com.company.scopery.modules.raid.raidaction.application.command.CompleteRaidActionCommand;
import com.company.scopery.modules.raid.raidaction.application.command.CreateLinkedTaskFromRaidActionCommand;
import com.company.scopery.modules.raid.raidaction.application.command.UpdateRaidActionCommand;
import com.company.scopery.modules.raid.raidaction.application.response.CreateLinkedTaskFromRaidActionResponse;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.application.service.RaidActionQueryService;
import com.company.scopery.modules.raid.raidaction.http.request.CompleteRaidActionRequest;
import com.company.scopery.modules.raid.raidaction.http.request.CreateLinkedTaskFromRaidActionRequest;
import com.company.scopery.modules.raid.raidaction.http.request.UpdateRaidActionRequest;
import com.company.scopery.modules.raid.shared.constant.RaidApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(RaidApiPaths.ACTIONS)
@Tag(name = "RAID - Actions")
public class RaidActionController {
    private final RaidActionQueryService query;
    private final CompleteRaidActionAction complete;
    private final UpdateRaidActionAction update;
    private final CancelRaidActionAction cancel;
    private final CreateLinkedTaskFromRaidActionAction createLinkedTask;

    public RaidActionController(RaidActionQueryService query, CompleteRaidActionAction complete,
                                UpdateRaidActionAction update, CancelRaidActionAction cancel,
                                CreateLinkedTaskFromRaidActionAction createLinkedTask) {
        this.query = query;
        this.complete = complete;
        this.update = update;
        this.cancel = cancel;
        this.createLinkedTask = createLinkedTask;
    }

    @GetMapping("/{raidActionId}")
    @Operation(summary = "Get RAID action")
    public ApiResponse<RaidActionResponse> get(@PathVariable UUID projectId, @PathVariable UUID raidActionId) {
        return ApiResponse.success(query.get(projectId, raidActionId));
    }

    @PutMapping("/{raidActionId}")
    @Operation(summary = "Update RAID action")
    public ApiResponse<RaidActionResponse> update(@PathVariable UUID projectId,
                                                    @PathVariable UUID raidActionId,
                                                    @Valid @RequestBody UpdateRaidActionRequest request) {
        return ApiResponse.success(update.execute(new UpdateRaidActionCommand(
                projectId, raidActionId, request.title(), request.description(),
                request.ownerUserId(), request.dueDate())));
    }

    @PostMapping("/{raidActionId}/cancel")
    @Operation(summary = "Cancel RAID action")
    public ApiResponse<RaidActionResponse> cancel(@PathVariable UUID projectId, @PathVariable UUID raidActionId) {
        return ApiResponse.success(cancel.execute(new CancelRaidActionCommand(projectId, raidActionId)));
    }

    @PostMapping("/{raidActionId}/complete")
    @Operation(summary = "Complete RAID action")
    public ApiResponse<RaidActionResponse> complete(@PathVariable UUID projectId,
                                                    @PathVariable UUID raidActionId,
                                                    @Valid @RequestBody(required = false) CompleteRaidActionRequest request) {
        String note = request == null ? null : request.completionNote();
        return ApiResponse.success(complete.execute(new CompleteRaidActionCommand(projectId, raidActionId, note)));
    }

    @PostMapping("/{raidActionId}/create-linked-task")
    @Operation(summary = "Create linked task from RAID action")
    public ApiResponse<CreateLinkedTaskFromRaidActionResponse> createLinkedTask(
            @PathVariable UUID projectId,
            @PathVariable UUID raidActionId,
            @Valid @RequestBody(required = false) CreateLinkedTaskFromRaidActionRequest request) {
        var req = request == null ? new CreateLinkedTaskFromRaidActionRequest(null, null, null) : request;
        return ApiResponse.success(createLinkedTask.execute(new CreateLinkedTaskFromRaidActionCommand(
                projectId, raidActionId, req.phaseId(), req.wbsNodeId(), req.estimateHours())));
    }
}
