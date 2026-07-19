package com.company.scopery.modules.productivity.navigation.domain.model;
import java.util.*; import java.util.UUID;
public interface UserNavigationPreferenceRepository {
    UserNavigationPreference save(UserNavigationPreference p);
    Optional<UserNavigationPreference> findByWorkspaceAndUser(UUID workspaceId, UUID userId);
}
