package com.company.scopery.modules.documenthub.generatedjob.application.response;

import java.util.List;

public record ProjectBriefPreviewResponse(
        Preview preview,
        String generationId,
        List<String> warnings
) {
    public record Preview(
            String title,
            List<Section> sections,
            List<String> assumptions
    ) {}

    public record Section(
            String heading,
            String body,
            List<String> bullets
    ) {}
}
