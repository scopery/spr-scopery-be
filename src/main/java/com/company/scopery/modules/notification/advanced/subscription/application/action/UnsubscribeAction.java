package com.company.scopery.modules.notification.advanced.subscription.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.shared.error.AdvancedNotificationExceptions;
import com.company.scopery.modules.notification.advanced.subscription.application.command.UnsubscribeCommand;
import com.company.scopery.modules.notification.advanced.subscription.application.response.NotificationSubscriptionResponse;
import com.company.scopery.modules.notification.advanced.subscription.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UnsubscribeAction {
    private final NotificationSubscriptionRepository repo; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public UnsubscribeAction(NotificationSubscriptionRepository repo, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.repo=repo; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional
    public NotificationSubscriptionResponse execute(UnsubscribeCommand c) {
        var user = currentUser.resolveCurrentUser();
        iam.requireWorkspaceAccess(c.workspaceId(), user.id(), IamAuthorities.NOTIFICATION_PREFERENCE_UPDATE);
        var s = repo.findById(c.subscriptionId()).orElseThrow(() -> AdvancedNotificationExceptions.subscriptionNotFound(c.subscriptionId()));
        if (!s.userId().equals(user.id())) throw AdvancedNotificationExceptions.accessDenied();
        return NotificationSubscriptionResponse.from(repo.save(s.unsubscribe()));
    }
}
