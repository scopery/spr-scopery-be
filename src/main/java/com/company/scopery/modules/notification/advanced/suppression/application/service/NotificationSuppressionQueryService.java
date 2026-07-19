package com.company.scopery.modules.notification.advanced.suppression.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.shared.error.AdvancedNotificationExceptions;
import com.company.scopery.modules.notification.advanced.suppression.application.response.NotificationSuppressionResponse;
import com.company.scopery.modules.notification.advanced.suppression.domain.model.NotificationSuppressionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class NotificationSuppressionQueryService {
    private final NotificationSuppressionRepository suppressions; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public NotificationSuppressionQueryService(NotificationSuppressionRepository suppressions, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.suppressions=suppressions; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional(readOnly=true)
    public List<NotificationSuppressionResponse> list(UUID workspaceId) {
        try { iam.requireWorkspaceAccess(workspaceId, currentUser.resolveCurrentUser().id(), IamAuthorities.NOTIFICATION_PREFERENCE_UPDATE); }
        catch (RuntimeException ex) { throw AdvancedNotificationExceptions.accessDenied(); }
        return suppressions.findByWorkspaceId(workspaceId).stream().map(NotificationSuppressionResponse::from).toList();
    }
}
