package com.company.scopery.modules.collaboration.agendaitem.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.agendaitem.application.action.*;
import com.company.scopery.modules.collaboration.agendaitem.application.command.*;
import com.company.scopery.modules.collaboration.agendaitem.application.response.MeetingAgendaItemResponse;
import com.company.scopery.modules.collaboration.agendaitem.application.service.MeetingAgendaItemQueryService;
import com.company.scopery.modules.collaboration.agendaitem.http.request.*;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(CollaborationApiPaths.MEETINGS + "/{meetingId}/agenda-items")
@Tag(name = "Collaboration - Agenda")
public class MeetingAgendaItemController {
    private final CreateAgendaItemAction create; private final UpdateAgendaItemAction update;
    private final DeleteAgendaItemAction delete; private final ReorderAgendaItemsAction reorder;
    private final MeetingAgendaItemQueryService query;
    public MeetingAgendaItemController(CreateAgendaItemAction create, UpdateAgendaItemAction update, DeleteAgendaItemAction delete,
                                        ReorderAgendaItemsAction reorder, MeetingAgendaItemQueryService query) {
        this.create=create; this.update=update; this.delete=delete; this.reorder=reorder; this.query=query;
    }
    @PostMapping @Operation(summary="Create agenda item")
    public ApiResponse<MeetingAgendaItemResponse> create(@PathVariable UUID projectId, @PathVariable UUID meetingId, @Valid @RequestBody CreateAgendaItemRequest r) {
        return ApiResponse.success(create.execute(new CreateAgendaItemCommand(projectId, meetingId, r.title(), r.description(), r.ownerUserId(), r.sortOrder(), r.timeboxMinutes(), r.clientVisible())));
    }
    @GetMapping @Operation(summary="List agenda items")
    public ApiResponse<List<MeetingAgendaItemResponse>> list(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(query.list(projectId, meetingId));
    }
    @PutMapping("/{agendaItemId}") @Operation(summary="Update agenda item")
    public ApiResponse<MeetingAgendaItemResponse> update(@PathVariable UUID projectId, @PathVariable UUID meetingId, @PathVariable UUID agendaItemId, @Valid @RequestBody UpdateAgendaItemRequest r) {
        return ApiResponse.success(update.execute(new UpdateAgendaItemCommand(projectId, meetingId, agendaItemId, r.title(), r.description(), r.ownerUserId(), r.status(), r.sortOrder(), r.timeboxMinutes(), r.clientVisible())));
    }
    @DeleteMapping("/{agendaItemId}") @Operation(summary="Archive agenda item")
    public ApiResponse<Void> delete(@PathVariable UUID projectId, @PathVariable UUID meetingId, @PathVariable UUID agendaItemId) {
        delete.execute(new DeleteAgendaItemCommand(projectId, meetingId, agendaItemId)); return ApiResponse.success(null);
    }
    @PutMapping("/reorder") @Operation(summary="Reorder agenda items")
    public ApiResponse<List<MeetingAgendaItemResponse>> reorder(@PathVariable UUID projectId, @PathVariable UUID meetingId, @Valid @RequestBody ReorderAgendaItemsRequest r) {
        return ApiResponse.success(reorder.execute(new ReorderAgendaItemsCommand(projectId, meetingId, r.orderedIds())));
    }
}
