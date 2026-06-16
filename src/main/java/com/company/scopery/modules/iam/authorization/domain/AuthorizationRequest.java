package com.company.scopery.modules.iam.authorization.domain;

import java.util.UUID;

public record AuthorizationRequest(UUID userId, UUID resourceId, String rightCode) {
}
