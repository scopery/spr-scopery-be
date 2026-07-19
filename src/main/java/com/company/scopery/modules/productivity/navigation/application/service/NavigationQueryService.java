package com.company.scopery.modules.productivity.navigation.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.navigation.application.response.*;
import com.company.scopery.modules.productivity.navigation.domain.model.*;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class NavigationQueryService {
    private final NavigationMenuDefinitionRepository menus; private final UserNavigationPreferenceRepository prefs;
    private final ProductivityAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser;
    public NavigationQueryService(NavigationMenuDefinitionRepository menus, UserNavigationPreferenceRepository prefs,
                                  ProductivityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.menus=menus; this.prefs=prefs; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional(readOnly=true)
    public List<NavigationMenuResponse> menu(UUID workspaceId) {
        authorization.requireNavView(workspaceId);
        return menus.findEnabled().stream().map(NavigationMenuResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public UserNavigationPreferenceResponse myPreference(UUID workspaceId) {
        authorization.requireNavView(workspaceId);
        var user = currentUser.resolveCurrentUser();
        return prefs.findByWorkspaceAndUser(workspaceId, user.id())
                .map(UserNavigationPreferenceResponse::from)
                .orElseGet(() -> new UserNavigationPreferenceResponse(null, null, null));
    }
}
