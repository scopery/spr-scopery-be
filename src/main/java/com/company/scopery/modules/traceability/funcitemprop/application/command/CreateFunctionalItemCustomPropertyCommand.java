package com.company.scopery.modules.traceability.funcitemprop.application.command;

import java.util.UUID;

public record CreateFunctionalItemCustomPropertyCommand(
        UUID functionalItemId,
        UUID projectId,
        String propKey,
        String propValue,
        String fieldType
) {}
