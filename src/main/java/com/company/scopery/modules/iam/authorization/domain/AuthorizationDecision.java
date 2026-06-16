package com.company.scopery.modules.iam.authorization.domain;

public record AuthorizationDecision(boolean allowed, AuthorizationDecisionReason reason) {

    public static AuthorizationDecision allow(AuthorizationDecisionReason reason) {
        return new AuthorizationDecision(true, reason);
    }

    public static AuthorizationDecision deny(AuthorizationDecisionReason reason) {
        return new AuthorizationDecision(false, reason);
    }
}
