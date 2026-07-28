package com.company.scopery.modules.traceability.functionalitem.application.response;

import java.util.UUID;

public record FunctionalItemImportCandidate(
        UUID existingId,
        String existingCode,
        String existingTitle,
        double similarity
) {}
