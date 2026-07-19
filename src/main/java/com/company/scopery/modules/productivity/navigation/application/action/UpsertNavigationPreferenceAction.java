package com.company.scopery.modules.productivity.navigation.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.navigation.application.response.UserNavigationPreferenceResponse;
import com.company.scopery.modules.productivity.navigation.domain.model.*;
import com.company.scopery.modules.productivity.shared.activity.ProductivityActivityLogger;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.shared.constant.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.productivity.navigation.application.command.UpsertNavigationPreferenceCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpsertNavigationPreferenceAction {
    private final UserNavigationPreferenceRepository prefs; private final ProductivityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final ProductivityActivityLogger activityLogger;
    public UpsertNavigationPreferenceAction(UserNavigationPreferenceRepository prefs, ProductivityAuthorizationService authorization,
                                            CurrentUserAuthorizationService currentUser, ProductivityActivityLogger activityLogger) {
        this.prefs=prefs; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public UserNavigationPreferenceResponse execute(UpsertNavigationPreferenceCommand c) {
        authorization.requireNavView(c.workspaceId());
        var user = currentUser.resolveCurrentUser();
        var existing = prefs.findByWorkspaceAndUser(c.workspaceId(), user.id());
        var saved = existing.map(p -> prefs.save(p.update(c.preferenceJson(), c.landing())))
                .orElseGet(() -> prefs.save(UserNavigationPreference.create(c.workspaceId(), user.id(), c.preferenceJson(), c.landing())));
        activityLogger.logSuccess("NAVIGATION_PREFERENCE", saved.id(), ProductivityActivityActions.NAV_PREF_UPDATED, "Navigation preference updated");
        return UserNavigationPreferenceResponse.from(saved);
    }
}
