package com.company.scopery.modules.eventregistry.eventdefinition.api.request;

public record SearchEventDefinitionRequest(
        String keyword,
        String sourceSystem,
        String eventKey,
        String status,
        int page,
        int size
) {}