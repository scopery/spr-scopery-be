package com.company.scopery.modules.notification.advanced.preference.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.preference.application.response.NotificationPreferenceProfileResponse;
import com.company.scopery.modules.notification.advanced.preference.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
public class NotificationPreferenceQueryService {
    private final NotificationPreferenceProfileRepository repo; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public NotificationPreferenceQueryService(NotificationPreferenceProfileRepository repo, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.repo=repo; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional(readOnly=true)
    public NotificationPreferenceProfileResponse me(UUID workspaceId) {
        var user = currentUser.resolveCurrentUser();
        iam.requireWorkspaceAccess(workspaceId, user.id(), IamAuthorities.NOTIFICATION_PREFERENCE_UPDATE);
        return NotificationPreferenceProfileResponse.from(repo.findByWorkspaceAndUser(workspaceId, user.id())
                .orElseGet(() -> NotificationPreferenceProfile.create(workspaceId, user.id())));
    }
}
