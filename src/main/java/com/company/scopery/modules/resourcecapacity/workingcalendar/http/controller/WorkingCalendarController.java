package com.company.scopery.modules.resourcecapacity.workingcalendar.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.action.ActivateWorkingCalendarAction;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.action.ArchiveWorkingCalendarAction;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.action.CreateWorkingCalendarAction;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.action.DeactivateWorkingCalendarAction;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.action.SetDefaultWorkingCalendarAction;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.action.UpdateWorkingCalendarAction;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.ActivateWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.ArchiveWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.CreateWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.DeactivateWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.SetDefaultWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.command.UpdateWorkingCalendarCommand;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.query.SearchWorkingCalendarQuery;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.response.WorkingCalendarResponse;
import com.company.scopery.modules.resourcecapacity.workingcalendar.application.service.WorkingCalendarQueryService;
import com.company.scopery.modules.resourcecapacity.workingcalendar.http.request.CreateWorkingCalendarRequest;
import com.company.scopery.modules.resourcecapacity.workingcalendar.http.request.UpdateWorkingCalendarRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(CapacityApiPaths.CALENDARS)
@Tag(name = "Resource Capacity - Working Calendars")
public class WorkingCalendarController {

    private final WorkingCalendarQueryService queryService;
    private final CreateWorkingCalendarAction createAction;
    private final UpdateWorkingCalendarAction updateAction;
    private final ActivateWorkingCalendarAction activateAction;
    private final DeactivateWorkingCalendarAction deactivateAction;
    private final ArchiveWorkingCalendarAction archiveAction;
    private final SetDefaultWorkingCalendarAction setDefaultAction;

    public WorkingCalendarController(WorkingCalendarQueryService queryService,
                                     CreateWorkingCalendarAction createAction,
                                     UpdateWorkingCalendarAction updateAction,
                                     ActivateWorkingCalendarAction activateAction,
                                     DeactivateWorkingCalendarAction deactivateAction,
                                     ArchiveWorkingCalendarAction archiveAction,
                                     SetDefaultWorkingCalendarAction setDefaultAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.archiveAction = archiveAction;
        this.setDefaultAction = setDefaultAction;
    }

    @PostMapping
    @Operation(summary = "Create a working calendar")
    public ApiResponse<WorkingCalendarResponse> create(
            @RequestParam UUID workspaceId,
            @Valid @RequestBody CreateWorkingCalendarRequest request) {
        var cmd = new CreateWorkingCalendarCommand(
                workspaceId, request.code(), request.name(), request.description(),
                request.timezone(), request.isDefault());
        return ApiResponse.success(createAction.execute(cmd));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a working calendar by ID")
    public ApiResponse<WorkingCalendarResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(queryService.getCalendar(id));
    }

    @GetMapping
    @Operation(summary = "Search working calendars")
    public ApiResponse<PageResponse<WorkingCalendarResponse>> search(
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isDefault,
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = new SearchWorkingCalendarQuery(workspaceId, status, isDefault, code, page, size);
        PageResult<WorkingCalendarResponse> result = queryService.searchCalendars(query);
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a working calendar")
    public ApiResponse<WorkingCalendarResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkingCalendarRequest request) {
        var cmd = new UpdateWorkingCalendarCommand(id, request.name(), request.description(), request.timezone());
        return ApiResponse.success(updateAction.execute(cmd));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a working calendar")
    public ApiResponse<WorkingCalendarResponse> activate(@PathVariable UUID id) {
        return ApiResponse.success(activateAction.execute(new ActivateWorkingCalendarCommand(id)));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a working calendar")
    public ApiResponse<WorkingCalendarResponse> deactivate(@PathVariable UUID id) {
        return ApiResponse.success(deactivateAction.execute(new DeactivateWorkingCalendarCommand(id)));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a working calendar")
    public ApiResponse<WorkingCalendarResponse> archive(@PathVariable UUID id) {
        return ApiResponse.success(archiveAction.execute(new ArchiveWorkingCalendarCommand(id)));
    }

    @PatchMapping("/{id}/set-default")
    @Operation(summary = "Set a working calendar as the workspace default")
    public ApiResponse<WorkingCalendarResponse> setDefault(@PathVariable UUID id) {
        return ApiResponse.success(setDefaultAction.execute(new SetDefaultWorkingCalendarCommand(id)));
    }
}
