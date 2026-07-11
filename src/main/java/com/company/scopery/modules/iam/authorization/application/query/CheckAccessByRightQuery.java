package com.company.scopery.modules.iam.authorization.application.query;

import java.util.UUID;

public record CheckAccessByRightQuery(UUID userId, UUID resourceId, String rightCode) {
}
