package com.company.scopery.modules.traceability.tracelink.application.response;

import java.util.List;
import java.util.UUID;

public record BatchTraceLinkResponse(
        List<TraceLinkResponse> created,
        List<SkippedLink> skipped,
        List<FailedLink> failed
) {
    public record SkippedLink(UUID sourceId, UUID targetId, String linkType, String reason) {}
    public record FailedLink(UUID sourceId, UUID targetId, String linkType, String reason) {}
}
