package com.company.scopery.modules.airecommendation.application.response;

import java.time.OffsetDateTime;

public record AcceptSuggestionResponse(
        String suggestionRef,
        String status,
        OffsetDateTime acceptedAt,
        boolean domainMutationPerformed,
        long version
) {}
