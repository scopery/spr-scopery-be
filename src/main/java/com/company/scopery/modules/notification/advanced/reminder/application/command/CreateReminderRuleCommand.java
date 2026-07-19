package com.company.scopery.modules.notification.advanced.reminder.application.command;
import java.util.UUID;
public record CreateReminderRuleCommand(UUID workspaceId, String ruleCode, String name, String conditionJson, String recipientRuleJson) {}
