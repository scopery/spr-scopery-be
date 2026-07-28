package com.company.scopery.modules.iam.authorization.application.response;

import java.util.Map;
import java.util.UUID;

public record CapabilitiesResponse(
        String resourceType,
        UUID resourceRefId,
        String pack,
        Map<String, Boolean> capabilities
) {}
