package com.company.scopery.modules.projectnotification.subscription.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.projectnotification.shared.authorization.ProjectNotificationAuthorizationService;
import com.company.scopery.modules.projectnotification.subscription.application.response.ProjectNotificationSubscriptionResponse;
import com.company.scopery.modules.projectnotification.subscription.domain.model.ProjectNotificationSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectSubscriptionQueryService {
    private final ProjectNotificationSubscriptionRepository subscriptions;
    private final ProjectNotificationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;

    public ProjectSubscriptionQueryService(ProjectNotificationSubscriptionRepository subscriptions,
                                           ProjectNotificationAuthorizationService authorization,
                                           CurrentUserAuthorizationService currentUser) {
        this.subscriptions = subscriptions;
        this.authorization = authorization;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public List<ProjectNotificationSubscriptionResponse> list(UUID projectId) {
        authorization.requireViewSubscriptions(projectId);
        return subscriptions.findByProjectId(projectId).stream()
                .map(ProjectNotificationSubscriptionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectNotificationSubscriptionResponse> listMine(UUID projectId) {
        authorization.requireSubscribeSelf(projectId);
        UUID userId = currentUser.resolveCurrentUser().id();
        return subscriptions.findByProjectIdAndSubscriberUserId(projectId, userId).stream()
                .map(ProjectNotificationSubscriptionResponse::from).toList();
    }
}
