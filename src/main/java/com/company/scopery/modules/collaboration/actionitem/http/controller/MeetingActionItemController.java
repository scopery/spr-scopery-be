package com.company.scopery.modules.collaboration.actionitem.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.actionitem.application.action.*;
import com.company.scopery.modules.collaboration.actionitem.application.command.*;
import com.company.scopery.modules.collaboration.actionitem.application.response.MeetingActionItemResponse;
import com.company.scopery.modules.collaboration.actionitem.application.service.MeetingActionItemQueryService;
import com.company.scopery.modules.collaboration.actionitem.http.request.*;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping(CollaborationApiPaths.ACTION_ITEMS) @Tag(name = "Collaboration - Action Items")
public class MeetingActionItemController {
    private final UpdateActionItemAction update; private final CompleteActionItemAction complete;
    private final CreateLinkedTaskAction linkTask; private final ArchiveActionItemAction archive; private final MeetingActionItemQueryService query;
    public MeetingActionItemController(UpdateActionItemAction update, CompleteActionItemAction complete, CreateLinkedTaskAction linkTask, ArchiveActionItemAction archive, MeetingActionItemQueryService query) {
        this.update=update; this.complete=complete; this.linkTask=linkTask; this.archive=archive; this.query=query;
    }
    @GetMapping("/{actionItemId}") @Operation(summary="Get action item")
    public ApiResponse<MeetingActionItemResponse> get(@PathVariable UUID projectId, @PathVariable UUID actionItemId) { return ApiResponse.success(query.get(projectId, actionItemId)); }
    @PutMapping("/{actionItemId}") @Operation(summary="Update action item")
    public ApiResponse<MeetingActionItemResponse> update(@PathVariable UUID projectId, @PathVariable UUID actionItemId, @Valid @RequestBody UpdateActionItemRequest r) {
        return ApiResponse.success(update.execute(new UpdateActionItemCommand(projectId, actionItemId, r.title(), r.description(), r.ownerTargetType(), r.ownerTargetId(), r.dueDate(), r.status(), r.clientVisible())));
    }
    @PostMapping("/{actionItemId}/complete") @Operation(summary="Complete action item")
    public ApiResponse<MeetingActionItemResponse> complete(@PathVariable UUID projectId, @PathVariable UUID actionItemId, @RequestBody(required=false) CompleteActionItemRequest r) {
        return ApiResponse.success(complete.execute(new CompleteActionItemCommand(projectId, actionItemId, r == null ? null : r.completionNote())));
    }
    @PostMapping("/{actionItemId}/create-linked-task") @Operation(summary="Link existing task to action item")
    public ApiResponse<MeetingActionItemResponse> linkTask(@PathVariable UUID projectId, @PathVariable UUID actionItemId, @Valid @RequestBody LinkTaskRequest r) {
        return ApiResponse.success(linkTask.execute(new CreateLinkedTaskCommand(projectId, actionItemId, r.taskId())));
    }
    @PatchMapping("/{actionItemId}/archive") @Operation(summary="Archive action item")
    public ApiResponse<MeetingActionItemResponse> archive(@PathVariable UUID projectId, @PathVariable UUID actionItemId) {
        return ApiResponse.success(archive.execute(new ArchiveActionItemCommand(projectId, actionItemId)));
    }
}
