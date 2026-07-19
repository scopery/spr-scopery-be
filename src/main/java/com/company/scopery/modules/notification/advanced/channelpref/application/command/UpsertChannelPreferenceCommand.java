package com.company.scopery.modules.notification.advanced.channelpref.application.command;
import java.util.UUID;
public record UpsertChannelPreferenceCommand(UUID workspaceId, String categoryCode, String channelCode, boolean enabled) {}
