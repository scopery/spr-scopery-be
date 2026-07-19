package com.company.scopery.modules.resourcecapacity.calendarexception.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.action.CreateCalendarExceptionAction;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.action.DeleteCalendarExceptionAction;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.action.UpdateCalendarExceptionAction;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.command.CreateCalendarExceptionCommand;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.command.DeleteCalendarExceptionCommand;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.command.UpdateCalendarExceptionCommand;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.query.SearchCalendarExceptionQuery;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.response.CalendarExceptionResponse;
import com.company.scopery.modules.resourcecapacity.calendarexception.application.service.CalendarExceptionQueryService;
import com.company.scopery.modules.resourcecapacity.calendarexception.http.request.CreateCalendarExceptionRequest;
import com.company.scopery.modules.resourcecapacity.calendarexception.http.request.UpdateCalendarExceptionRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping(CapacityApiPaths.CALENDAR_EXCEPTIONS)
@Tag(name = "Resource Capacity - Calendar Exceptions")
public class CalendarExceptionController {

    private final CalendarExceptionQueryService queryService;
    private final CreateCalendarExceptionAction createAction;
    private final UpdateCalendarExceptionAction updateAction;
    private final DeleteCalendarExceptionAction deleteAction;

    public CalendarExceptionController(CalendarExceptionQueryService queryService,
                                       CreateCalendarExceptionAction createAction,
                                       UpdateCalendarExceptionAction updateAction,
                                       DeleteCalendarExceptionAction deleteAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
    }

    @PostMapping
    @Operation(summary = "Create a calendar exception")
    public ApiResponse<CalendarExceptionResponse> create(
            @PathVariable UUID calendarId,
            @Valid @RequestBody CreateCalendarExceptionRequest request) {
        var cmd = new CreateCalendarExceptionCommand(
                calendarId, request.exceptionDate(), request.exceptionType(), request.name(),
                request.description(), request.isWorkingDay(), request.workingHours());
        return ApiResponse.success(createAction.execute(cmd));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a calendar exception by ID")
    public ApiResponse<CalendarExceptionResponse> get(
            @PathVariable UUID calendarId,
            @PathVariable UUID id) {
        return ApiResponse.success(queryService.getException(calendarId, id));
    }

    @GetMapping
    @Operation(summary = "Search calendar exceptions within a date range")
    public ApiResponse<PageResponse<CalendarExceptionResponse>> search(
            @PathVariable UUID calendarId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = new SearchCalendarExceptionQuery(calendarId, from, to, page, size);
        PageResult<CalendarExceptionResponse> result = queryService.searchExceptions(query);
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a calendar exception")
    public ApiResponse<CalendarExceptionResponse> update(
            @PathVariable UUID calendarId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCalendarExceptionRequest request) {
        var cmd = new UpdateCalendarExceptionCommand(
                id, calendarId, request.exceptionType(), request.name(),
                request.description(), request.isWorkingDay(), request.workingHours());
        return ApiResponse.success(updateAction.execute(cmd));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a calendar exception")
    public ApiResponse<Void> delete(
            @PathVariable UUID calendarId,
            @PathVariable UUID id) {
        deleteAction.execute(new DeleteCalendarExceptionCommand(id, calendarId));
        return ApiResponse.success(null);
    }
}
