package com.company.scopery.modules.traceability.businessrule.application.command;

import java.util.UUID;

public record CreateBusinessRuleCommand(
        UUID functionalItemId,
        UUID projectId,
        String code,
        String title,
        String description,
        String severity
) {}
