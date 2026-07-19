package com.company.scopery.modules.projectnotification.preference.domain.model;

import com.company.scopery.modules.projectnotification.preference.domain.enums.PreferenceChannel;

import java.time.Instant;
import java.util.UUID;

public record ProjectNotificationPreference(
        UUID id,
        UUID projectId,
        UUID taskId,
        UUID workspaceId,
        UUID userId,
        String eventCode,
        PreferenceChannel channel,
        boolean enabled,
        boolean muted,
        boolean mandatoryOverride,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectNotificationPreference upsert(
            UUID id, UUID projectId, UUID taskId, UUID workspaceId, UUID userId,
            String eventCode, PreferenceChannel channel, boolean enabled, boolean muted) {
        Instant now = Instant.now();
        return new ProjectNotificationPreference(
                id == null ? UUID.randomUUID() : id,
                projectId, taskId, workspaceId, userId, eventCode, channel,
                enabled, muted, false, 0, now, now);
    }

    public boolean shouldDeliver(boolean mandatoryRule) {
        if (mandatoryRule) {
            return true;
        }
        if (muted || !enabled) {
            return false;
        }
        return true;
    }
}
