package com.company.scopery.modules.traceability.businessrule.application.command;

import java.util.UUID;

public record UpdateBusinessRuleCommand(
        UUID id,
        UUID functionalItemId,
        UUID projectId,
        String title,
        String description,
        String severity,
        String status
) {}
