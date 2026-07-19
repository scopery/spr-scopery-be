package com.company.scopery.modules.notification.advanced.channelpref.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.channelpref.application.command.UpsertChannelPreferenceCommand;
import com.company.scopery.modules.notification.advanced.channelpref.application.response.NotificationChannelPreferenceResponse;
import com.company.scopery.modules.notification.advanced.channelpref.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpsertChannelPreferenceAction {
    private final NotificationChannelPreferenceRepository repo; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public UpsertChannelPreferenceAction(NotificationChannelPreferenceRepository repo, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.repo=repo; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional
    public NotificationChannelPreferenceResponse execute(UpsertChannelPreferenceCommand c) {
        var user = currentUser.resolveCurrentUser();
        iam.requireWorkspaceAccess(c.workspaceId(), user.id(), IamAuthorities.NOTIFICATION_PREFERENCE_UPDATE);
        var existing = repo.findOne(c.workspaceId(), user.id(), c.categoryCode().trim(), c.channelCode().trim());
        var saved = repo.save(existing
                .map(p -> p.withEnabled(c.enabled()))
                .orElseGet(() -> NotificationChannelPreference.create(c.workspaceId(), user.id(), c.categoryCode().trim(), c.channelCode().trim(), c.enabled())));
        return NotificationChannelPreferenceResponse.from(saved);
    }
}
