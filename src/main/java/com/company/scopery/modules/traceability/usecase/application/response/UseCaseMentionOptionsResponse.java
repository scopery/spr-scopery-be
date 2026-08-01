package com.company.scopery.modules.traceability.usecase.application.response;

import java.util.List;
import java.util.UUID;

public record UseCaseMentionOptionsResponse(
        List<MentionOption> items,
        int limit,
        String mode
) {
    public record MentionOption(
            String entityType,
            UUID entityId,
            String label,
            String parentLabel,
            UUID parentId,
            UUID screenId
    ) {}
}
