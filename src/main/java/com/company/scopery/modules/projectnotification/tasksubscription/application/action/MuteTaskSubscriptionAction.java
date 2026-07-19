package com.company.scopery.modules.projectnotification.tasksubscription.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.activity.ProjectNotificationActivityLogger;
import com.company.scopery.modules.projectnotification.shared.authorization.ProjectNotificationAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationActivityActions;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationEntityTypes;
import com.company.scopery.modules.projectnotification.shared.error.ProjectNotificationExceptions;
import com.company.scopery.modules.projectnotification.tasksubscription.application.response.TaskNotificationSubscriptionResponse;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscription;
import com.company.scopery.modules.projectnotification.tasksubscription.domain.model.TaskNotificationSubscriptionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class MuteTaskSubscriptionAction {
    private final TaskNotificationSubscriptionRepository subscriptions;
    private final ProjectNotificationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectNotificationActivityLogger activityLogger;

    public MuteTaskSubscriptionAction(TaskNotificationSubscriptionRepository subscriptions,
                                      ProjectNotificationAuthorizationService authorization,
                                      CurrentUserAuthorizationService currentUser,
                                      ProjectNotificationActivityLogger activityLogger) {
        this.subscriptions = subscriptions;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public TaskNotificationSubscriptionResponse execute(UUID projectId, UUID taskId, UUID subscriptionId, boolean mute) {
        TaskNotificationSubscription sub = subscriptions.findById(subscriptionId)
                .orElseThrow(() -> ProjectNotificationExceptions.taskSubscriptionNotFound(subscriptionId));
        if (!sub.projectId().equals(projectId) || !sub.taskId().equals(taskId)) {
            throw ProjectNotificationExceptions.taskSubscriptionNotFound(subscriptionId);
        }
        UUID actorId = currentUser.resolveCurrentUser().id();
        if (!sub.subscriberUserId().equals(actorId)) {
            authorization.requireTaskManage(projectId);
        } else {
            authorization.requireTaskSubscribeSelf(projectId);
        }
        sub = mute ? sub.mute() : sub.unmute();
        sub = subscriptions.save(sub);
        activityLogger.logSuccess(ProjectNotificationEntityTypes.TASK_SUBSCRIPTION, sub.id(),
                mute ? ProjectNotificationActivityActions.MUTE_TASK_SUBSCRIPTION
                        : ProjectNotificationActivityActions.UNMUTE_TASK_SUBSCRIPTION,
                mute ? "Muted" : "Unmuted");
        return TaskNotificationSubscriptionResponse.from(sub);
    }
}
