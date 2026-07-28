package com.company.scopery.modules.traceability.functionalitem.application.response;

import com.company.scopery.modules.traceability.functionalitem.http.request.ImportFunctionalItemEntry;

import java.util.Map;
import java.util.UUID;

public record FunctionalItemImportDiff(
        UUID existingId,
        String existingCode,
        String existingTitle,
        ImportFunctionalItemEntry incoming,
        Map<String, Object[]> changes
) {}
