package com.company.scopery.modules.notification.advanced.reminder.application.command;
import java.util.UUID;
public record DismissReminderInstanceCommand(UUID workspaceId, UUID reminderInstanceId) {}
