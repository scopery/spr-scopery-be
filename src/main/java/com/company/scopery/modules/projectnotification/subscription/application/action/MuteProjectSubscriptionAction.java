package com.company.scopery.modules.projectnotification.subscription.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.activity.ProjectNotificationActivityLogger;
import com.company.scopery.modules.projectnotification.shared.authorization.ProjectNotificationAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationActivityActions;
import com.company.scopery.modules.projectnotification.shared.constant.ProjectNotificationEntityTypes;
import com.company.scopery.modules.projectnotification.shared.error.ProjectNotificationExceptions;
import com.company.scopery.modules.projectnotification.subscription.application.response.ProjectNotificationSubscriptionResponse;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscription;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscriptionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class MuteProjectSubscriptionAction {
    private final ProjectNotificationSubscriptionRepository subscriptions;
    private final ProjectNotificationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectNotificationActivityLogger activityLogger;

    public MuteProjectSubscriptionAction(ProjectNotificationSubscriptionRepository subscriptions,
                                         ProjectNotificationAuthorizationService authorization,
                                         CurrentUserAuthorizationService currentUser,
                                         ProjectNotificationActivityLogger activityLogger) {
        this.subscriptions = subscriptions;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectNotificationSubscriptionResponse execute(UUID projectId, UUID subscriptionId, boolean mute) {
        ProjectNotificationSubscription sub = subscriptions.findById(subscriptionId)
                .orElseThrow(() -> ProjectNotificationExceptions.subscriptionNotFound(subscriptionId));
        if (!sub.projectId().equals(projectId)) {
            throw ProjectNotificationExceptions.subscriptionNotFound(subscriptionId);
        }
        UUID actorId = currentUser.resolveCurrentUser().id();
        if (!sub.subscriberUserId().equals(actorId)) {
            authorization.requireManageSubscribers(projectId);
        } else {
            authorization.requireSubscribeSelf(projectId);
        }
        sub = mute ? sub.mute() : sub.unmute();
        sub = subscriptions.save(sub);
        activityLogger.logSuccess(ProjectNotificationEntityTypes.PROJECT_SUBSCRIPTION, sub.id(),
                mute ? ProjectNotificationActivityActions.MUTE_PROJECT_SUBSCRIPTION
                        : ProjectNotificationActivityActions.UNMUTE_PROJECT_SUBSCRIPTION,
                mute ? "Muted" : "Unmuted");
        return ProjectNotificationSubscriptionResponse.from(sub);
    }
}
