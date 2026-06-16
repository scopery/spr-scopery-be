package com.company.scopery.modules.iam.grant.application.response;

import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRight;

import java.time.Instant;
import java.util.UUID;

public record IamAccessGrantRightResponse(UUID grantId, UUID rightId, Instant createdAt) {

    public static IamAccessGrantRightResponse from(IamAccessGrantRight domain) {
        return new IamAccessGrantRightResponse(domain.grantId(), domain.rightId(), domain.createdAt());
    }
}
