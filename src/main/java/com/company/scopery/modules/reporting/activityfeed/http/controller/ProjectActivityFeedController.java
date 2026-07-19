package com.company.scopery.modules.reporting.activityfeed.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.reporting.activityfeed.application.response.ActivityFeedItemResponse;
import com.company.scopery.modules.reporting.activityfeed.application.service.ProjectActivityFeedQueryService;
import com.company.scopery.modules.reporting.shared.constant.ReportingApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ReportingApiPaths.ACTIVITY_FEED)
@Tag(name = "Reporting - Activity Feed")
public class ProjectActivityFeedController {

    private final ProjectActivityFeedQueryService queryService;

    public ProjectActivityFeedController(ProjectActivityFeedQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "List project activity feed entries")
    public ApiResponse<PageResponse<ActivityFeedItemResponse>> list(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<ActivityFeedItemResponse> result = queryService.list(projectId, page, size);
        return ApiResponse.success(PageResponse.fromDomain(result));
    }
}
