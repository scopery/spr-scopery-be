package com.company.scopery.modules.traceability.funcitemprop.application.command;

import java.util.UUID;

public record UpdateFunctionalItemCustomPropertyCommand(
        UUID id,
        UUID functionalItemId,
        UUID projectId,
        String propValue,
        String fieldType
) {}
