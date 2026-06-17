package com.company.scopery.modules.notification.emailrule.application.command;

import java.util.UUID;

public record UpdateEmailRuleCommand(
        UUID id,
        String name,
        String description,
        String recipientStrategy,
        String recipientConfigJson,
        int priority
) {}
