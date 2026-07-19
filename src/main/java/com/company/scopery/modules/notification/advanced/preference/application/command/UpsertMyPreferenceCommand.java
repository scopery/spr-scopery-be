package com.company.scopery.modules.notification.advanced.preference.application.command;
import java.util.UUID;
public record UpsertMyPreferenceCommand(UUID workspaceId, String timezone, String defaultMode, Boolean digestEnabled, Boolean quietHoursEnabled, String quietHoursStart, String quietHoursEnd) {}
