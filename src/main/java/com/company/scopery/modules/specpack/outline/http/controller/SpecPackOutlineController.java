package com.company.scopery.modules.specpack.outline.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.specpack.outline.application.action.ApproveOutlineAction;
import com.company.scopery.modules.specpack.outline.application.action.CreateOutlineAction;
import com.company.scopery.modules.specpack.outline.application.command.ApproveOutlineCommand;
import com.company.scopery.modules.specpack.outline.application.command.CreateOutlineCommand;
import com.company.scopery.modules.specpack.outline.application.response.OutlineResponse;
import com.company.scopery.modules.specpack.outline.application.service.OutlineQueryService;
import com.company.scopery.modules.specpack.outline.http.request.CreateOutlineRequest;
import com.company.scopery.modules.specpack.shared.constant.SpecPackApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Spec Pack - Outlines")
@RestController
@RequestMapping(SpecPackApiPaths.OUTLINES)
public class SpecPackOutlineController {

    private final CreateOutlineAction createAction;
    private final ApproveOutlineAction approveAction;
    private final OutlineQueryService queryService;

    public SpecPackOutlineController(CreateOutlineAction createAction,
                                      ApproveOutlineAction approveAction,
                                      OutlineQueryService queryService) {
        this.createAction = createAction;
        this.approveAction = approveAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a new outline for an agent session")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OutlineResponse> create(@PathVariable UUID projectId,
                                               @PathVariable UUID sessionId,
                                               @Valid @RequestBody CreateOutlineRequest request) {
        return ApiResponse.success(createAction.execute(new CreateOutlineCommand(
                projectId, sessionId, request.outlineJson()
        )));
    }

    @Operation(summary = "List outlines for an agent session")
    @GetMapping
    public ApiResponse<List<OutlineResponse>> list(@PathVariable UUID projectId,
                                                   @PathVariable UUID sessionId) {
        return ApiResponse.success(queryService.listBySession(sessionId));
    }

    @Operation(summary = "Get an outline by ID")
    @GetMapping("/{outlineId}")
    public ApiResponse<OutlineResponse> getById(@PathVariable UUID projectId,
                                                @PathVariable UUID sessionId,
                                                @PathVariable UUID outlineId) {
        return ApiResponse.success(queryService.getById(outlineId));
    }

    @Operation(summary = "Approve a draft outline")
    @PostMapping("/{outlineId}/approve")
    public ApiResponse<OutlineResponse> approve(@PathVariable UUID projectId,
                                                @PathVariable UUID sessionId,
                                                @PathVariable UUID outlineId) {
        return ApiResponse.success(approveAction.execute(new ApproveOutlineCommand(
                projectId, sessionId, outlineId
        )));
    }
}
