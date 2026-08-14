package com.company.scopery.modules.traceability.screenspecdoc.domain.model;

import java.util.UUID;

public record SpecDocScreen(
        UUID documentId,
        UUID screenId,
        int displayOrder,
        String note) {}
