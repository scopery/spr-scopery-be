package com.company.scopery.modules.notification.advanced.channelpref.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.channelpref.application.response.NotificationChannelPreferenceResponse;
import com.company.scopery.modules.notification.advanced.channelpref.domain.model.NotificationChannelPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;

@Service
public class ChannelPreferenceQueryService {
    private final NotificationChannelPreferenceRepository repo;
    private final CurrentUserAuthorizationService currentUser;
    private final WorkspaceIamIntegrationService iam;

    public ChannelPreferenceQueryService(NotificationChannelPreferenceRepository repo,
                                         CurrentUserAuthorizationService currentUser,
                                         WorkspaceIamIntegrationService iam) {
        this.repo = repo; this.currentUser = currentUser; this.iam = iam;
    }

    @Transactional(readOnly = true)
    public List<NotificationChannelPreferenceResponse> me(UUID workspaceId) {
        var user = currentUser.resolveCurrentUser();
        iam.requireWorkspaceAccess(workspaceId, user.id(), IamAuthorities.NOTIFICATION_PREFERENCE_UPDATE);
        return repo.findByWorkspaceIdAndUserId(workspaceId, user.id()).stream().map(NotificationChannelPreferenceResponse::from).toList();
    }
}
