package com.company.scopery.modules.clientportal.portalmeeting.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.comment.application.action.CreateClientCommentAction;
import com.company.scopery.modules.clientportal.comment.application.command.CreateClientCommentCommand;
import com.company.scopery.modules.clientportal.comment.application.response.ClientCommentResponse;
import com.company.scopery.modules.clientportal.comment.application.service.ClientCommentQueryService;
import com.company.scopery.modules.clientportal.portalmeeting.http.request.CreatePortalMeetingCommentRequest;
import com.company.scopery.modules.clientportal.portalmeeting.application.response.PortalMeetingMinutesResponse;
import com.company.scopery.modules.clientportal.portalmeeting.application.service.PortalMeetingQueryService;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import com.company.scopery.modules.collaboration.meeting.application.response.MeetingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;

@RestController
@RequestMapping(ClientPortalApiPaths.PORTAL_PROJECTS + "/{projectId}/meetings")
@Tag(name = "Client Portal - Project Meetings")
public class PortalProjectMeetingController {
    private final PortalMeetingQueryService meetings;
    private final ClientCommentQueryService comments;
    private final CreateClientCommentAction createComment;

    public PortalProjectMeetingController(PortalMeetingQueryService meetings, ClientCommentQueryService comments,
                                          CreateClientCommentAction createComment) {
        this.meetings = meetings; this.comments = comments; this.createComment = createComment;
    }

    @GetMapping @Operation(summary = "List client-visible meetings")
    public ApiResponse<List<MeetingResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(meetings.list(projectId));
    }

    @GetMapping("/{meetingId}") @Operation(summary = "Get client-visible meeting")
    public ApiResponse<MeetingResponse> get(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(meetings.get(projectId, meetingId));
    }

    @GetMapping("/{meetingId}/minutes") @Operation(summary = "List client-visible meeting minutes summaries")
    public ApiResponse<List<PortalMeetingMinutesResponse>> minutes(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(meetings.listMinutes(projectId, meetingId));
    }

    @GetMapping("/{meetingId}/comments") @Operation(summary = "List portal comments on meeting")
    public ApiResponse<List<ClientCommentResponse>> listComments(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        meetings.get(projectId, meetingId); // grant + visibility
        return ApiResponse.success(comments.listPortalByTarget(projectId, "MEETING", meetingId));
    }

    @PostMapping("/{meetingId}/comments") @Operation(summary = "Add portal comment on meeting")
    public ApiResponse<ClientCommentResponse> createComment(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                                            @Valid @RequestBody CreatePortalMeetingCommentRequest r) {
        meetings.get(projectId, meetingId);
        return ApiResponse.success(createComment.execute(
                new CreateClientCommentCommand(projectId, "MEETING", meetingId, r.body())));
    }
}
