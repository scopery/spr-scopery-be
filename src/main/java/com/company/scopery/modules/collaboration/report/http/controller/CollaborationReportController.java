package com.company.scopery.modules.collaboration.report.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.report.application.response.CollaborationReportResponses.*;
import com.company.scopery.modules.collaboration.report.application.service.CollaborationReportQueryService;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(CollaborationApiPaths.REPORTS) @Tag(name = "Collaboration - Reports")
public class CollaborationReportController {
    private final CollaborationReportQueryService query;
    public CollaborationReportController(CollaborationReportQueryService query) { this.query=query; }
    @GetMapping("/meetings") @Operation(summary="Meeting register report")
    public ApiResponse<List<MeetingRegisterRow>> meetings(@PathVariable UUID projectId) { return ApiResponse.success(query.meetings(projectId)); }
    @GetMapping("/meeting-actions") @Operation(summary="Meeting actions report")
    public ApiResponse<List<MeetingActionReportRow>> actions(@PathVariable UUID projectId) { return ApiResponse.success(query.actions(projectId, false)); }
    @GetMapping("/overdue-meeting-actions") @Operation(summary="Overdue meeting actions")
    public ApiResponse<List<MeetingActionReportRow>> overdue(@PathVariable UUID projectId) { return ApiResponse.success(query.actions(projectId, true)); }
    @GetMapping("/meeting-minutes-status") @Operation(summary="Minutes status report")
    public ApiResponse<List<MinutesStatusRow>> minutes(@PathVariable UUID projectId) { return ApiResponse.success(query.minutesStatus(projectId)); }
    @GetMapping("/comment-activity") @Operation(summary="Comment activity report")
    public ApiResponse<List<CommentActivityRow>> comments(@PathVariable UUID projectId) { return ApiResponse.success(query.commentActivity(projectId)); }
}
