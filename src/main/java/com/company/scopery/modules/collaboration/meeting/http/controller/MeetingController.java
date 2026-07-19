package com.company.scopery.modules.collaboration.meeting.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.meeting.application.action.*;
import com.company.scopery.modules.collaboration.meeting.application.command.*;
import com.company.scopery.modules.collaboration.meeting.application.response.MeetingResponse;
import com.company.scopery.modules.collaboration.meeting.application.service.MeetingQueryService;
import com.company.scopery.modules.collaboration.meeting.http.request.*;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(CollaborationApiPaths.MEETINGS)
@Tag(name = "Collaboration - Meetings")
public class MeetingController {
    private final CreateMeetingAction create;
    private final UpdateMeetingAction update;
    private final StartMeetingAction start;
    private final CompleteMeetingAction complete;
    private final CancelMeetingAction cancel;
    private final ArchiveMeetingAction archive;
    private final MeetingQueryService query;
    public MeetingController(CreateMeetingAction create, UpdateMeetingAction update, StartMeetingAction start,
                             CompleteMeetingAction complete, CancelMeetingAction cancel, ArchiveMeetingAction archive,
                             MeetingQueryService query) {
        this.create = create; this.update = update; this.start = start; this.complete = complete;
        this.cancel = cancel; this.archive = archive; this.query = query;
    }
    @PostMapping @Operation(summary = "Create meeting")
    public ApiResponse<MeetingResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateMeetingRequest request) {
        return ApiResponse.success(create.execute(new CreateMeetingCommand(
                projectId, request.meetingSeriesId(), request.title(), request.description(), request.meetingType(),
                request.startAt(), request.endAt(), request.timezone(), request.location(), request.meetingUrl(),
                request.organizerUserId(), request.clientVisible())));
    }
    @GetMapping @Operation(summary = "List meetings")
    public ApiResponse<List<MeetingResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }
    @GetMapping("/{meetingId}") @Operation(summary = "Get meeting")
    public ApiResponse<MeetingResponse> get(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(query.get(projectId, meetingId));
    }
    @PutMapping("/{meetingId}") @Operation(summary = "Update meeting")
    public ApiResponse<MeetingResponse> update(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                               @Valid @RequestBody UpdateMeetingRequest request) {
        return ApiResponse.success(update.execute(new UpdateMeetingCommand(
                projectId, meetingId, request.title(), request.description(), request.meetingType(),
                request.startAt(), request.endAt(), request.timezone(), request.location(), request.meetingUrl(),
                request.clientVisible())));
    }
    @PostMapping("/{meetingId}/start") @Operation(summary = "Start meeting")
    public ApiResponse<MeetingResponse> start(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(start.execute(new StartMeetingCommand(projectId, meetingId)));
    }
    @PostMapping("/{meetingId}/complete") @Operation(summary = "Complete meeting")
    public ApiResponse<MeetingResponse> complete(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(complete.execute(new CompleteMeetingCommand(projectId, meetingId)));
    }
    @PostMapping("/{meetingId}/cancel") @Operation(summary = "Cancel meeting")
    public ApiResponse<MeetingResponse> cancel(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                               @RequestBody(required = false) CancelMeetingRequest request) {
        return ApiResponse.success(cancel.execute(new CancelMeetingCommand(projectId, meetingId, request == null ? null : request.reason())));
    }
    @PatchMapping("/{meetingId}/archive") @Operation(summary = "Archive meeting")
    public ApiResponse<MeetingResponse> archive(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(archive.execute(new ArchiveMeetingCommand(projectId, meetingId)));
    }
}
