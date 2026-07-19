package com.company.scopery.modules.eventregistry.eventdefinition.http.request;

import java.util.UUID;

public record DeprecateEventDefinitionRequest(
        UUID replacementEventDefinitionId,
        String reason
) {}
