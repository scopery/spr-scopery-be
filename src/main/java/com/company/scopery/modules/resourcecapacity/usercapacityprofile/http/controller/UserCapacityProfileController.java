package com.company.scopery.modules.resourcecapacity.usercapacityprofile.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action.ActivateUserCapacityProfileAction;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action.ArchiveUserCapacityProfileAction;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action.CreateUserCapacityProfileAction;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action.DeactivateUserCapacityProfileAction;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.action.UpdateUserCapacityProfileAction;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.ActivateUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.ArchiveUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.CreateUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.DeactivateUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.command.UpdateUserCapacityProfileCommand;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.query.SearchUserCapacityProfileQuery;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.response.UserCapacityProfileResponse;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.application.service.UserCapacityProfileQueryService;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.http.request.CreateUserCapacityProfileRequest;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.http.request.UpdateUserCapacityProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(CapacityApiPaths.USER_PROFILES)
@Tag(name = "Resource Capacity - User Capacity Profiles")
public class UserCapacityProfileController {

    private final UserCapacityProfileQueryService queryService;
    private final CreateUserCapacityProfileAction createAction;
    private final UpdateUserCapacityProfileAction updateAction;
    private final ActivateUserCapacityProfileAction activateAction;
    private final DeactivateUserCapacityProfileAction deactivateAction;
    private final ArchiveUserCapacityProfileAction archiveAction;

    public UserCapacityProfileController(UserCapacityProfileQueryService queryService,
                                         CreateUserCapacityProfileAction createAction,
                                         UpdateUserCapacityProfileAction updateAction,
                                         ActivateUserCapacityProfileAction activateAction,
                                         DeactivateUserCapacityProfileAction deactivateAction,
                                         ArchiveUserCapacityProfileAction archiveAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.archiveAction = archiveAction;
    }

    @PostMapping
    @Operation(summary = "Create a user capacity profile")
    public ApiResponse<UserCapacityProfileResponse> create(
            @RequestParam UUID workspaceId,
            @Valid @RequestBody CreateUserCapacityProfileRequest request) {
        var cmd = new CreateUserCapacityProfileCommand(
                workspaceId, request.workspaceMemberId(), request.workingCalendarId(),
                request.defaultDailyHours(), request.focusFactor(),
                request.effectiveFrom(), request.effectiveTo());
        return ApiResponse.success(createAction.execute(cmd));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user capacity profile by ID")
    public ApiResponse<UserCapacityProfileResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(queryService.getProfile(id));
    }

    @GetMapping
    @Operation(summary = "Search user capacity profiles")
    public ApiResponse<PageResponse<UserCapacityProfileResponse>> search(
            @RequestParam UUID workspaceId,
            @RequestParam(required = false) UUID workspaceMemberId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = new SearchUserCapacityProfileQuery(workspaceId, workspaceMemberId, userId, status, page, size);
        PageResult<UserCapacityProfileResponse> result = queryService.searchProfiles(query);
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user capacity profile")
    public ApiResponse<UserCapacityProfileResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserCapacityProfileRequest request) {
        var cmd = new UpdateUserCapacityProfileCommand(
                id, request.workingCalendarId(), request.defaultDailyHours(), request.focusFactor(),
                request.effectiveFrom(), request.effectiveTo());
        return ApiResponse.success(updateAction.execute(cmd));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a user capacity profile")
    public ApiResponse<UserCapacityProfileResponse> activate(@PathVariable UUID id) {
        return ApiResponse.success(activateAction.execute(new ActivateUserCapacityProfileCommand(id)));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a user capacity profile")
    public ApiResponse<UserCapacityProfileResponse> deactivate(@PathVariable UUID id) {
        return ApiResponse.success(deactivateAction.execute(new DeactivateUserCapacityProfileCommand(id)));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a user capacity profile")
    public ApiResponse<UserCapacityProfileResponse> archive(@PathVariable UUID id) {
        return ApiResponse.success(archiveAction.execute(new ArchiveUserCapacityProfileCommand(id)));
    }
}
