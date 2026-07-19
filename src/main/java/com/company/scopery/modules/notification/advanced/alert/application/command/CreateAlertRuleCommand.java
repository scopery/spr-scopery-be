package com.company.scopery.modules.notification.advanced.alert.application.command;
import java.util.UUID;
public record CreateAlertRuleCommand(UUID workspaceId, String ruleCode, String name, String category, String conditionJson, Boolean bypassQuietHours) {}
