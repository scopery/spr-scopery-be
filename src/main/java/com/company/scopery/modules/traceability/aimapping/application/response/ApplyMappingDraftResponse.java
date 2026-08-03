package com.company.scopery.modules.traceability.aimapping.application.response;

public record ApplyMappingDraftResponse(
        int created,
        int skippedStale,
        int skippedConflict,
        int failed
) {}
