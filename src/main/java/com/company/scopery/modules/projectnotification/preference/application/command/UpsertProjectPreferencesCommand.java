package com.company.scopery.modules.projectnotification.preference.application.command;

import java.util.List;
import java.util.UUID;

public record UpsertProjectPreferencesCommand(
        UUID projectId, UUID taskId, List<PreferenceItem> preferences
) {
    public record PreferenceItem(String eventCode, String channel, boolean enabled, boolean muted) {}
}
