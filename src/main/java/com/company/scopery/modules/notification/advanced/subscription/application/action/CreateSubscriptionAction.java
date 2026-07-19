package com.company.scopery.modules.notification.advanced.subscription.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.subscription.application.command.CreateSubscriptionCommand;
import com.company.scopery.modules.notification.advanced.subscription.application.response.NotificationSubscriptionResponse;
import com.company.scopery.modules.notification.advanced.subscription.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateSubscriptionAction {
    private final NotificationSubscriptionRepository repo; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public CreateSubscriptionAction(NotificationSubscriptionRepository repo, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.repo=repo; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional
    public NotificationSubscriptionResponse execute(CreateSubscriptionCommand c) {
        var user = currentUser.resolveCurrentUser();
        iam.requireWorkspaceAccess(c.workspaceId(), user.id(), IamAuthorities.NOTIFICATION_PREFERENCE_UPDATE);
        return NotificationSubscriptionResponse.from(repo.save(NotificationSubscription.create(c.workspaceId(), user.id(), c.targetType(), c.targetId(), c.subscriptionLevel())));
    }
}
