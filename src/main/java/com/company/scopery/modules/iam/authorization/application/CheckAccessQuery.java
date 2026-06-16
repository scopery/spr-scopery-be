package com.company.scopery.modules.iam.authorization.application;

import java.util.UUID;

public record CheckAccessQuery(UUID userId, UUID resourceId, String rightCode) {
}
