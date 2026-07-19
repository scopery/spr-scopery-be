package com.company.scopery.modules.collaboration.participant.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.participant.application.action.*;
import com.company.scopery.modules.collaboration.participant.application.command.*;
import com.company.scopery.modules.collaboration.participant.application.response.MeetingParticipantResponse;
import com.company.scopery.modules.collaboration.participant.application.service.MeetingParticipantQueryService;
import com.company.scopery.modules.collaboration.participant.http.request.*;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(CollaborationApiPaths.MEETINGS + "/{meetingId}/participants")
@Tag(name = "Collaboration - Participants")
public class MeetingParticipantController {
    private final AddParticipantAction add; private final UpdateParticipantAction update;
    private final RemoveParticipantAction remove; private final MarkParticipantAttendedAction markAttended;
    private final MeetingParticipantQueryService query;
    public MeetingParticipantController(AddParticipantAction add, UpdateParticipantAction update, RemoveParticipantAction remove,
                                        MarkParticipantAttendedAction markAttended, MeetingParticipantQueryService query) {
        this.add=add; this.update=update; this.remove=remove; this.markAttended=markAttended; this.query=query;
    }
    @PostMapping @Operation(summary="Add participant")
    public ApiResponse<MeetingParticipantResponse> add(@PathVariable UUID projectId, @PathVariable UUID meetingId, @Valid @RequestBody AddParticipantRequest r) {
        return ApiResponse.success(add.execute(new AddParticipantCommand(projectId, meetingId, r.targetType(), r.targetId(), r.displayName(), r.email(), r.participantRole(), r.clientVisible())));
    }
    @GetMapping @Operation(summary="List participants")
    public ApiResponse<List<MeetingParticipantResponse>> list(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(query.list(projectId, meetingId));
    }
    @PutMapping("/{participantId}") @Operation(summary="Update participant")
    public ApiResponse<MeetingParticipantResponse> update(@PathVariable UUID projectId, @PathVariable UUID meetingId, @PathVariable UUID participantId, @Valid @RequestBody UpdateParticipantRequest r) {
        return ApiResponse.success(update.execute(new UpdateParticipantCommand(projectId, meetingId, participantId, r.participantRole(), r.attendanceStatus(), r.clientVisible())));
    }
    @DeleteMapping("/{participantId}") @Operation(summary="Remove participant")
    public ApiResponse<Void> remove(@PathVariable UUID projectId, @PathVariable UUID meetingId, @PathVariable UUID participantId) {
        remove.execute(new RemoveParticipantCommand(projectId, meetingId, participantId)); return ApiResponse.success(null);
    }
    @PostMapping("/{participantId}/mark-attended") @Operation(summary="Mark attended")
    public ApiResponse<MeetingParticipantResponse> markAttended(@PathVariable UUID projectId, @PathVariable UUID meetingId, @PathVariable UUID participantId) {
        return ApiResponse.success(markAttended.execute(new MarkParticipantAttendedCommand(projectId, meetingId, participantId)));
    }
}
