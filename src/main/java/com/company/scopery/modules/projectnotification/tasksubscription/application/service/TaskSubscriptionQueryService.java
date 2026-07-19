package com.company.scopery.modules.projectnotification.tasksubscription.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.authorization.ProjectNotificationAuthorizationService;
import com.company.scopery.modules.projectnotification.tasksubscription.application.response.TaskNotificationSubscriptionResponse;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskSubscriptionQueryService {
    private final TaskNotificationSubscriptionRepository subscriptions;
    private final ProjectNotificationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;

    public TaskSubscriptionQueryService(TaskNotificationSubscriptionRepository subscriptions,
                                        ProjectNotificationAuthorizationService authorization,
                                        CurrentUserAuthorizationService currentUser) {
        this.subscriptions = subscriptions;
        this.authorization = authorization;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public List<TaskNotificationSubscriptionResponse> list(UUID projectId, UUID taskId) {
        authorization.requireTaskView(projectId);
        return subscriptions.findByTaskId(taskId).stream()
                .map(TaskNotificationSubscriptionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TaskNotificationSubscriptionResponse> listMine(UUID projectId, UUID taskId) {
        authorization.requireTaskSubscribeSelf(projectId);
        UUID userId = currentUser.resolveCurrentUser().id();
        return subscriptions.findByTaskIdAndSubscriberUserId(taskId, userId).stream()
                .map(TaskNotificationSubscriptionResponse::from).toList();
    }
}
