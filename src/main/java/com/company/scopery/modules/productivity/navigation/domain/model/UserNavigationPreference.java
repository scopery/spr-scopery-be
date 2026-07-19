package com.company.scopery.modules.productivity.navigation.domain.model;
import java.time.Instant; import java.util.UUID;
public record UserNavigationPreference(UUID id, UUID workspaceId, UUID userId, String preferenceJson, String defaultLandingRoute,
        int version, Instant createdAt, Instant updatedAt) {
    public static UserNavigationPreference create(UUID workspaceId, UUID userId, String preferenceJson, String landing) {
        Instant now = Instant.now();
        return new UserNavigationPreference(UUID.randomUUID(), workspaceId, userId, preferenceJson, landing, 0, now, now);
    }
    public UserNavigationPreference update(String preferenceJson, String landing) {
        return new UserNavigationPreference(id, workspaceId, userId,
                preferenceJson != null ? preferenceJson : this.preferenceJson,
                landing != null ? landing : this.defaultLandingRoute,
                version, createdAt, Instant.now());
    }
}
