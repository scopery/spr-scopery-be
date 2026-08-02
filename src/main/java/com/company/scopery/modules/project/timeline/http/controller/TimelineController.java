package com.company.scopery.modules.project.timeline.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.timeline.application.query.TimelineViewQuery;
import com.company.scopery.modules.project.timeline.application.response.TimelineViewResponse;
import com.company.scopery.modules.project.timeline.application.service.TimelineQueryService;
import com.company.scopery.modules.project.timeline.domain.enums.TimelineGranularity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping(ProjectApiPaths.TIMELINE)
@Tag(name = "Project - Timeline")
public class TimelineController {

    private final TimelineQueryService queryService;

    public TimelineController(TimelineQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "Get timeline cell buckets for a project")
    public ApiResponse<TimelineViewResponse> getTimeline(
            @PathVariable UUID projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "DAY") String granularity) {
        TimelineGranularity gran = ProjectEnumParser.parseRequired(
                TimelineGranularity.class, granularity, "TIMELINE_INVALID_GRANULARITY", "granularity");
        return ApiResponse.success(queryService.getView(new TimelineViewQuery(projectId, from, to, gran)));
    }
}
