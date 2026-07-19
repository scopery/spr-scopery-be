package com.company.scopery.modules.reporting.activityfeed.application.response;

import java.time.Instant;

public record ActivityFeedItemResponse(
        Instant timestamp,
        String actorId,
        String actorName,
        String action,
        String message
) {}
