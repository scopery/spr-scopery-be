package com.company.scopery.modules.productivity.navigation.application.response;
import com.company.scopery.modules.productivity.navigation.domain.model.UserNavigationPreference;
import java.util.UUID;
public record UserNavigationPreferenceResponse(UUID id, String preferenceJson, String defaultLandingRoute) {
    public static UserNavigationPreferenceResponse from(UserNavigationPreference p) {
        return new UserNavigationPreferenceResponse(p.id(), p.preferenceJson(), p.defaultLandingRoute());
    }
}
