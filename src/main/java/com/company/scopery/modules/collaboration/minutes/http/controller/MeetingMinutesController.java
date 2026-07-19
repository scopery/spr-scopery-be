package com.company.scopery.modules.collaboration.minutes.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.minutes.application.action.*;
import com.company.scopery.modules.collaboration.minutes.application.command.*;
import com.company.scopery.modules.collaboration.minutes.application.response.MeetingMinutesResponse;
import com.company.scopery.modules.collaboration.minutes.application.service.MeetingMinutesQueryService;
import com.company.scopery.modules.collaboration.minutes.http.request.*;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(CollaborationApiPaths.MEETINGS + "/{meetingId}/minutes")
@Tag(name = "Collaboration - Minutes")
public class MeetingMinutesController {
    private final CreateMinutesAction create; private final UpdateMinutesAction update;
    private final SubmitMinutesAction submit; private final ApproveMinutesAction approve; private final RejectMinutesAction reject;
    private final GenerateMinutesDocumentAction generateDocument;
    private final MeetingMinutesQueryService query;
    public MeetingMinutesController(CreateMinutesAction create, UpdateMinutesAction update, SubmitMinutesAction submit,
                                    ApproveMinutesAction approve, RejectMinutesAction reject,
                                    GenerateMinutesDocumentAction generateDocument, MeetingMinutesQueryService query) {
        this.create=create; this.update=update; this.submit=submit; this.approve=approve; this.reject=reject;
        this.generateDocument=generateDocument; this.query=query;
    }
    @PostMapping @Operation(summary="Create minutes")
    public ApiResponse<MeetingMinutesResponse> create(@PathVariable UUID projectId, @PathVariable UUID meetingId, @RequestBody CreateMinutesRequest r) {
        return ApiResponse.success(create.execute(new CreateMinutesCommand(projectId, meetingId, r.summary(), r.decisionsSummary(), r.actionsSummary(), r.clientVisibleSummary())));
    }
    @GetMapping @Operation(summary="List minutes")
    public ApiResponse<List<MeetingMinutesResponse>> list(@PathVariable UUID projectId, @PathVariable UUID meetingId) {
        return ApiResponse.success(query.list(projectId, meetingId));
    }
    @PutMapping("/{minutesId}") @Operation(summary="Update minutes")
    public ApiResponse<MeetingMinutesResponse> update(@PathVariable UUID projectId, @PathVariable UUID meetingId, @PathVariable UUID minutesId, @RequestBody UpdateMinutesRequest r) {
        return ApiResponse.success(update.execute(new UpdateMinutesCommand(projectId, minutesId, r.summary(), r.decisionsSummary(), r.actionsSummary(), r.clientVisibleSummary())));
    }
    @PostMapping("/{minutesId}/submit-review") @Operation(summary="Submit minutes for review")
    public ApiResponse<MeetingMinutesResponse> submit(@PathVariable UUID projectId, @PathVariable UUID meetingId, @PathVariable UUID minutesId) {
        return ApiResponse.success(submit.execute(new SubmitMinutesCommand(projectId, minutesId)));
    }
    @PostMapping("/{minutesId}/approve") @Operation(summary="Approve minutes")
    public ApiResponse<MeetingMinutesResponse> approve(@PathVariable UUID projectId, @PathVariable UUID meetingId, @PathVariable UUID minutesId) {
        return ApiResponse.success(approve.execute(new ApproveMinutesCommand(projectId, minutesId)));
    }
    @PostMapping("/{minutesId}/reject") @Operation(summary="Reject minutes")
    public ApiResponse<MeetingMinutesResponse> reject(@PathVariable UUID projectId, @PathVariable UUID meetingId, @PathVariable UUID minutesId, @RequestBody(required=false) RejectMinutesRequest r) {
        return ApiResponse.success(reject.execute(new RejectMinutesCommand(projectId, minutesId, r == null ? null : r.reason())));
    }
    @PostMapping("/{minutesId}/generate-document") @Operation(summary="Generate DocHub document from minutes")
    public ApiResponse<MeetingMinutesResponse> generateDocument(@PathVariable UUID projectId, @PathVariable UUID meetingId,
                                                                @PathVariable UUID minutesId,
                                                                @RequestBody(required=false) GenerateMinutesDocumentRequest r) {
        var req = r == null ? new GenerateMinutesDocumentRequest(null, null, null) : r;
        return ApiResponse.success(generateDocument.execute(new GenerateMinutesDocumentCommand(projectId, meetingId, minutesId, req.folderId(), req.code(), req.title())));
    }
}
