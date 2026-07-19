package com.company.scopery.modules.projectnotification.tasksubscription.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationApiPaths;
import com.company.scopery.modules.projectnotification.tasksubscription.application.action.*;
import com.company.scopery.modules.projectnotification.tasksubscription.application.command.CreateTaskSubscriptionCommand;
import com.company.scopery.modules.projectnotification.tasksubscription.application.response.TaskNotificationSubscriptionResponse;
import com.company.scopery.modules.projectnotification.tasksubscription.application.service.TaskSubscriptionQueryService;
import com.company.scopery.modules.projectnotification.tasksubscription.http.request.CreateTaskSubscriptionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProjectNotificationApiPaths.TASK_SUBSCRIPTIONS)
@Tag(name = "Project Notification - Task Subscriptions")
public class TaskNotificationSubscriptionController {
    private final CreateTaskSubscriptionAction createAction;
    private final MuteTaskSubscriptionAction muteAction;
    private final DeleteTaskSubscriptionAction deleteAction;
    private final TaskSubscriptionQueryService queryService;

    public TaskNotificationSubscriptionController(
            CreateTaskSubscriptionAction createAction,
            MuteTaskSubscriptionAction muteAction,
            DeleteTaskSubscriptionAction deleteAction,
            TaskSubscriptionQueryService queryService) {
        this.createAction = createAction;
        this.muteAction = muteAction;
        this.deleteAction = deleteAction;
        this.queryService = queryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Subscribe to task notifications")
    public ApiResponse<TaskNotificationSubscriptionResponse> create(
            @PathVariable UUID projectId, @PathVariable UUID taskId,
            @Valid @RequestBody CreateTaskSubscriptionRequest request) {
        return ApiResponse.success(createAction.execute(new CreateTaskSubscriptionCommand(
                projectId, taskId, request.subscriberUserId(), request.subscriptionType())));
    }

    @GetMapping
    @Operation(summary = "List task notification subscriptions")
    public ApiResponse<List<TaskNotificationSubscriptionResponse>> list(
            @PathVariable UUID projectId, @PathVariable UUID taskId) {
        return ApiResponse.success(queryService.list(projectId, taskId));
    }

    @GetMapping("/me")
    @Operation(summary = "List my task notification subscriptions")
    public ApiResponse<List<TaskNotificationSubscriptionResponse>> listMine(
            @PathVariable UUID projectId, @PathVariable UUID taskId) {
        return ApiResponse.success(queryService.listMine(projectId, taskId));
    }

    @PatchMapping("/{subscriptionId}/mute")
    public ApiResponse<TaskNotificationSubscriptionResponse> mute(
            @PathVariable UUID projectId, @PathVariable UUID taskId, @PathVariable UUID subscriptionId) {
        return ApiResponse.success(muteAction.execute(projectId, taskId, subscriptionId, true));
    }

    @PatchMapping("/{subscriptionId}/unmute")
    public ApiResponse<TaskNotificationSubscriptionResponse> unmute(
            @PathVariable UUID projectId, @PathVariable UUID taskId, @PathVariable UUID subscriptionId) {
        return ApiResponse.success(muteAction.execute(projectId, taskId, subscriptionId, false));
    }

    @DeleteMapping("/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID projectId, @PathVariable UUID taskId, @PathVariable UUID subscriptionId) {
        deleteAction.execute(projectId, taskId, subscriptionId);
    }
}
