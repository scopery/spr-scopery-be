package com.company.scopery.modules.traceability.functionalitem.domain.model;

import java.util.UUID;

public record FunctionalItemTitleMatch(UUID id, String code, String title, double similarity) {}
