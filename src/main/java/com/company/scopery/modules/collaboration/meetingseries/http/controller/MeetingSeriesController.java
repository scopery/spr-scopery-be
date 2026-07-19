package com.company.scopery.modules.collaboration.meetingseries.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.meetingseries.application.action.*;
import com.company.scopery.modules.collaboration.meetingseries.application.command.*;
import com.company.scopery.modules.collaboration.meetingseries.application.response.MeetingSeriesResponse;
import com.company.scopery.modules.collaboration.meetingseries.application.service.MeetingSeriesQueryService;
import com.company.scopery.modules.collaboration.meetingseries.http.request.*;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(CollaborationApiPaths.MEETING_SERIES) @Tag(name = "Collaboration - Meeting Series")
public class MeetingSeriesController {
    private final CreateMeetingSeriesAction create; private final UpdateMeetingSeriesAction update;
    private final PauseMeetingSeriesAction pause; private final ArchiveMeetingSeriesAction archive;
    private final MeetingSeriesQueryService query;
    public MeetingSeriesController(CreateMeetingSeriesAction create, UpdateMeetingSeriesAction update,
                                   PauseMeetingSeriesAction pause, ArchiveMeetingSeriesAction archive,
                                   MeetingSeriesQueryService query) {
        this.create=create; this.update=update; this.pause=pause; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary="Create meeting series")
    public ApiResponse<MeetingSeriesResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateMeetingSeriesRequest r) {
        return ApiResponse.success(create.execute(new CreateMeetingSeriesCommand(projectId, r.code(), r.title(), r.description(), r.cadence(), r.ownerUserId(), r.clientVisible())));
    }
    @GetMapping @Operation(summary="List meeting series")
    public ApiResponse<List<MeetingSeriesResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{seriesId}") @Operation(summary="Get meeting series")
    public ApiResponse<MeetingSeriesResponse> get(@PathVariable UUID projectId, @PathVariable UUID seriesId) { return ApiResponse.success(query.get(projectId, seriesId)); }
    @PutMapping("/{seriesId}") @Operation(summary="Update meeting series")
    public ApiResponse<MeetingSeriesResponse> update(@PathVariable UUID projectId, @PathVariable UUID seriesId, @Valid @RequestBody UpdateMeetingSeriesRequest r) {
        return ApiResponse.success(update.execute(new UpdateMeetingSeriesCommand(projectId, seriesId, r.title(), r.description(), r.cadence(), r.ownerUserId(), r.clientVisible())));
    }
    @PatchMapping("/{seriesId}/pause") @Operation(summary="Pause meeting series")
    public ApiResponse<MeetingSeriesResponse> pause(@PathVariable UUID projectId, @PathVariable UUID seriesId) { return ApiResponse.success(pause.execute(new PauseMeetingSeriesCommand(projectId, seriesId))); }
    @PatchMapping("/{seriesId}/archive") @Operation(summary="Archive meeting series")
    public ApiResponse<MeetingSeriesResponse> archive(@PathVariable UUID projectId, @PathVariable UUID seriesId) { return ApiResponse.success(archive.execute(new ArchiveMeetingSeriesCommand(projectId, seriesId))); }
}
