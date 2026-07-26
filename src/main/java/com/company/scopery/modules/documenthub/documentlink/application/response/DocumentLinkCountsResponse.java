package com.company.scopery.modules.documenthub.documentlink.application.response;

import java.util.Map;
import java.util.UUID;

public record DocumentLinkCountsResponse(
        Map<UUID, Long> counts
) {}
