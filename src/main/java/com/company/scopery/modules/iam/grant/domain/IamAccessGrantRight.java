package com.company.scopery.modules.iam.grant.domain;

import java.time.Instant;
import java.util.UUID;

public record IamAccessGrantRight(UUID grantId, UUID rightId, Instant createdAt) {

    public static IamAccessGrantRight create(UUID grantId, UUID rightId) {
        return new IamAccessGrantRight(grantId, rightId, Instant.now());
    }
}
