package com.company.scopery.modules.iam.authorization.application.response;

import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationDecision;

import java.util.UUID;

public record AuthorizationDecisionResponse(
        UUID userId,
        UUID resourceId,
        String rightCode,
        boolean allowed,
        String reason) {

    public static AuthorizationDecisionResponse from(UUID userId, UUID resourceId,
                                                      String rightCode, AuthorizationDecision decision) {
        return new AuthorizationDecisionResponse(
                userId, resourceId, rightCode, decision.allowed(), decision.reason().name());
    }
}
