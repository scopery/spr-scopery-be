package com.company.scopery.modules.notification.advanced.alert.application.command;
import java.util.UUID;
public record DismissAlertEventCommand(UUID workspaceId, UUID alertEventId) {}
