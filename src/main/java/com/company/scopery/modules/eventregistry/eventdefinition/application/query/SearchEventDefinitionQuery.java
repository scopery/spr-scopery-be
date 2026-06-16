package com.company.scopery.modules.eventregistry.eventdefinition.application.query;

public record SearchEventDefinitionQuery(
        String keyword,
        String sourceSystem,
        String eventKey,
        String status,
        int page,
        int size
) {}