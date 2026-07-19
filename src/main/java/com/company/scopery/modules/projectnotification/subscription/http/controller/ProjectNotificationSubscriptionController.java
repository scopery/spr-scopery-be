package com.company.scopery.modules.projectnotification.subscription.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationApiPaths;
import com.company.scopery.modules.projectnotification.subscription.application.action.CreateProjectSubscriptionAction;
import com.company.scopery.modules.projectnotification.subscription.application.action.DeleteProjectSubscriptionAction;
import com.company.scopery.modules.projectnotification.subscription.application.action.MuteProjectSubscriptionAction;
import com.company.scopery.modules.projectnotification.subscription.application.command.CreateProjectSubscriptionCommand;
import com.company.scopery.modules.projectnotification.subscription.application.response.ProjectNotificationSubscriptionResponse;
import com.company.scopery.modules.projectnotification.subscription.application.service.ProjectSubscriptionQueryService;
import com.company.scopery.modules.projectnotification.subscription.http.request.CreateProjectSubscriptionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectNotificationApiPaths.PROJECT_SUBSCRIPTIONS)
@Tag(name = "Project Notification - Subscriptions")
public class ProjectNotificationSubscriptionController {
    private final CreateProjectSubscriptionAction createAction;
    private final MuteProjectSubscriptionAction muteAction;
    private final DeleteProjectSubscriptionAction deleteAction;
    private final ProjectSubscriptionQueryService queryService;

    public ProjectNotificationSubscriptionController(
            CreateProjectSubscriptionAction createAction,
            MuteProjectSubscriptionAction muteAction,
            DeleteProjectSubscriptionAction deleteAction,
            ProjectSubscriptionQueryService queryService) {
        this.createAction = createAction;
        this.muteAction = muteAction;
        this.deleteAction = deleteAction;
        this.queryService = queryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Subscribe to project notifications")
    public ApiResponse<ProjectNotificationSubscriptionResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateProjectSubscriptionRequest request) {
        return ApiResponse.success(createAction.execute(new CreateProjectSubscriptionCommand(
                projectId, request.subscriberUserId(), request.subscriptionType())));
    }

    @GetMapping
    @Operation(summary = "List project notification subscriptions")
    public ApiResponse<List<ProjectNotificationSubscriptionResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(queryService.list(projectId));
    }

    @GetMapping("/me")
    @Operation(summary = "List my project notification subscriptions")
    public ApiResponse<List<ProjectNotificationSubscriptionResponse>> listMine(@PathVariable UUID projectId) {
        return ApiResponse.success(queryService.listMine(projectId));
    }

    @PatchMapping("/{subscriptionId}/mute")
    @Operation(summary = "Mute project notification subscription")
    public ApiResponse<ProjectNotificationSubscriptionResponse> mute(
            @PathVariable UUID projectId, @PathVariable UUID subscriptionId) {
        return ApiResponse.success(muteAction.execute(projectId, subscriptionId, true));
    }

    @PatchMapping("/{subscriptionId}/unmute")
    @Operation(summary = "Unmute project notification subscription")
    public ApiResponse<ProjectNotificationSubscriptionResponse> unmute(
            @PathVariable UUID projectId, @PathVariable UUID subscriptionId) {
        return ApiResponse.success(muteAction.execute(projectId, subscriptionId, false));
    }

    @DeleteMapping("/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete/unsubscribe project notification subscription")
    public void delete(@PathVariable UUID projectId, @PathVariable UUID subscriptionId) {
        deleteAction.execute(projectId, subscriptionId);
    }
}
