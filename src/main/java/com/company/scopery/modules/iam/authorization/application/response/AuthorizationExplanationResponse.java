package com.company.scopery.modules.iam.authorization.application.response;

import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationExplanation;
import java.util.List;
import java.util.UUID;

public record AuthorizationExplanationResponse(
        String permissionCode, String actionCode, String resourceType, UUID resourceRefId,
        boolean allowed, String reason, List<UUID> contributingGrantIds, String explanation) {
    public static AuthorizationExplanationResponse from(
            String permissionCode, String actionCode, String resourceType, UUID resourceRefId,
            AuthorizationExplanation explanation) {
        return new AuthorizationExplanationResponse(permissionCode, actionCode, resourceType, resourceRefId,
                explanation.allowed(), explanation.reason(), explanation.contributingGrantIds(),
                explanation.explanation());
    }
}
