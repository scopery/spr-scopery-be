package com.company.scopery.modules.notification.advanced.subscription.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.subscription.application.response.NotificationSubscriptionResponse;
import com.company.scopery.modules.notification.advanced.subscription.domain.model.NotificationSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class NotificationSubscriptionQueryService {
    private final NotificationSubscriptionRepository repo; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public NotificationSubscriptionQueryService(NotificationSubscriptionRepository repo, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.repo=repo; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional(readOnly=true)
    public List<NotificationSubscriptionResponse> listMine(UUID workspaceId) {
        var user = currentUser.resolveCurrentUser();
        iam.requireWorkspaceAccess(workspaceId, user.id(), IamAuthorities.NOTIFICATION_PREFERENCE_UPDATE);
        return repo.findActiveByUser(workspaceId, user.id()).stream().map(NotificationSubscriptionResponse::from).toList();
    }
}
