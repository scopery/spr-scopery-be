package com.company.scopery.modules.eventregistry.eventdefinition.application.command;

import java.util.UUID;

public record DeprecateEventDefinitionCommand(
        UUID id,
        UUID replacementEventDefinitionId,
        String reason
) {}
