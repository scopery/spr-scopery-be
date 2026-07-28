package com.company.scopery.modules.reporting.activityfeed.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.reporting.activityfeed.application.response.ScopedActivityFeedItemResponse;
import com.company.scopery.modules.reporting.activityfeed.application.service.OrganizationActivityFeedQueryService;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Organization - Activity Feed")
public class OrganizationActivityFeedController {

    private final OrganizationActivityFeedQueryService queryService;

    public OrganizationActivityFeedController(OrganizationActivityFeedQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping(WorkspaceApiPaths.ORGANIZATION_ACTIVITY_FEED)
    @Operation(summary = "List organization activity feed (requires ORGANIZATION_MANAGEMENT.MANAGE)")
    public ApiResponse<PageResponse<ScopedActivityFeedItemResponse>> list(
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<ScopedActivityFeedItemResponse> result = queryService.list(organizationId, page, size);
        return ApiResponse.success(PageResponse.fromDomain(result));
    }
}
