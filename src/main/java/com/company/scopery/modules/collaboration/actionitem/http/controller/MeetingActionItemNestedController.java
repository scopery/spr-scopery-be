package com.company.scopery.modules.collaboration.actionitem.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.actionitem.application.action.CreateActionItemAction;
import com.company.scopery.modules.collaboration.actionitem.application.command.CreateActionItemCommand;
import com.company.scopery.modules.collaboration.actionitem.application.response.MeetingActionItemResponse;
import com.company.scopery.modules.collaboration.actionitem.application.service.MeetingActionItemQueryService;
import com.company.scopery.modules.collaboration.actionitem.http.request.CreateActionItemRequest;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(CollaborationApiPaths.MEETINGS + "/{meetingId}/action-items") @Tag(name = "Collaboration - Action Items")
public class MeetingActionItemNestedController {
    private final CreateActionItemAction create; private final MeetingActionItemQueryService query;
    public MeetingActionItemNestedController(CreateActionItemAction create, MeetingActionItemQueryService query) { this.create=create; this.query=query; }
    @PostMapping @Operation(summary="Create action item")
    public ApiResponse<MeetingActionItemResponse> create(@PathVariable UUID projectId, @PathVariable UUID meetingId, @Valid @RequestBody CreateActionItemRequest r) {
        return ApiResponse.success(create.execute(new CreateActionItemCommand(projectId, meetingId, r.agendaItemId(), r.title(), r.description(), r.ownerTargetType(), r.ownerTargetId(), r.dueDate(), r.clientVisible())));
    }
    @GetMapping @Operation(summary="List meeting action items")
    public ApiResponse<List<MeetingActionItemResponse>> list(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(query.listByMeeting(projectId, meetingId));
    }
}
