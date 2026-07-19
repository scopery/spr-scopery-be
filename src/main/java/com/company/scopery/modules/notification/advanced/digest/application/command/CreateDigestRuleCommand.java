package com.company.scopery.modules.notification.advanced.digest.application.command;
import java.util.UUID;
public record CreateDigestRuleCommand(UUID workspaceId, String code, String name, String scope, String frequency, String scheduleConfigJson) {}
