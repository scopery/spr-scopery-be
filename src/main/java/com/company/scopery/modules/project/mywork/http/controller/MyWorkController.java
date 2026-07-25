package com.company.scopery.modules.project.mywork.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.mywork.application.query.MyWorkQuery;
import com.company.scopery.modules.project.mywork.application.response.MyWorkResponse;
import com.company.scopery.modules.project.mywork.application.service.MyWorkQueryService;
import com.company.scopery.modules.project.mywork.domain.enums.MyWorkWindow;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(WorkspaceApiPaths.MY_WORK)
@Tag(name = "My Work")
public class MyWorkController {

    private final MyWorkQueryService queryService;

    public MyWorkController(MyWorkQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "Get tasks assigned to the current user in a workspace")
    public ApiResponse<MyWorkResponse> getMyWork(
            @PathVariable UUID workspaceId,
            @RequestParam(defaultValue = "THIS_WEEK") MyWorkWindow window,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(defaultValue = "false") boolean includeCompleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        if (window == MyWorkWindow.CUSTOM && (dateFrom == null || dateTo == null)) {
            throw new com.company.scopery.common.exception.ValidationException(
                    "dateFrom and dateTo are required when window=CUSTOM");
        }
        if (dateFrom != null && dateTo != null && dateTo.isBefore(dateFrom)) {
            throw new com.company.scopery.common.exception.ValidationException(
                    "dateTo must not be before dateFrom");
        }
        if (size > 100) {
            throw new com.company.scopery.common.exception.ValidationException(
                    "size must not exceed 100");
        }

        MyWorkQuery query = new MyWorkQuery(
                workspaceId, window, dateFrom, dateTo,
                status, projectId, includeCompleted, page, size);

        return ApiResponse.success(queryService.query(query));
    }
}
