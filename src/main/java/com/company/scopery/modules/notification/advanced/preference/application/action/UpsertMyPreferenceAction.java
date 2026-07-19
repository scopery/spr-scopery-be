package com.company.scopery.modules.notification.advanced.preference.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.preference.application.command.UpsertMyPreferenceCommand;
import com.company.scopery.modules.notification.advanced.preference.application.response.NotificationPreferenceProfileResponse;
import com.company.scopery.modules.notification.advanced.preference.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpsertMyPreferenceAction {
    private final NotificationPreferenceProfileRepository repo; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public UpsertMyPreferenceAction(NotificationPreferenceProfileRepository repo, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.repo=repo; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional
    public NotificationPreferenceProfileResponse execute(UpsertMyPreferenceCommand c) {
        var user = currentUser.resolveCurrentUser();
        iam.requireWorkspaceAccess(c.workspaceId(), user.id(), IamAuthorities.NOTIFICATION_PREFERENCE_UPDATE);
        var profile = repo.findByWorkspaceAndUser(c.workspaceId(), user.id()).orElseGet(() -> NotificationPreferenceProfile.create(c.workspaceId(), user.id()));
        profile = repo.save(profile.update(
                c.timezone() != null ? c.timezone() : profile.timezone(),
                c.defaultMode() != null ? c.defaultMode() : profile.defaultMode(),
                c.digestEnabled() != null ? c.digestEnabled() : profile.digestEnabled(),
                c.quietHoursEnabled() != null ? c.quietHoursEnabled() : profile.quietHoursEnabled(),
                c.quietHoursStart() != null ? c.quietHoursStart() : profile.quietHoursStart(),
                c.quietHoursEnd() != null ? c.quietHoursEnd() : profile.quietHoursEnd()));
        return NotificationPreferenceProfileResponse.from(profile);
    }
}
