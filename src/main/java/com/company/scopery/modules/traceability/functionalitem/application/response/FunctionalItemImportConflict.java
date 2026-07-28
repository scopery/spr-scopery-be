package com.company.scopery.modules.traceability.functionalitem.application.response;

import com.company.scopery.modules.traceability.functionalitem.http.request.ImportFunctionalItemEntry;

import java.util.List;

public record FunctionalItemImportConflict(
        ImportFunctionalItemEntry incoming,
        List<FunctionalItemImportCandidate> candidates
) {}
