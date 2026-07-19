package com.company.scopery.modules.raid.decision.http.request;

import java.util.UUID;

public record SupersedeDecisionRequest(
        UUID replacementDecisionId,
        String title,
        String rationale,
        String category
) {}
