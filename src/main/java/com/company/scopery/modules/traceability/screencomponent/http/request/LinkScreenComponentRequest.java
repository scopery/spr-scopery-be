package com.company.scopery.modules.traceability.screencomponent.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LinkScreenComponentRequest(
        @NotNull UUID componentId,
        UUID sectionId,
        int displayOrder,
        String note) {}
