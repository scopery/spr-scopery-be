package com.company.scopery.modules.iam.authorization.domain.model;

import java.util.List;
import java.util.UUID;

public record AuthorizationExplanation(
        boolean allowed,
        String reason,
        List<UUID> contributingGrantIds,
        String explanation) {
}
