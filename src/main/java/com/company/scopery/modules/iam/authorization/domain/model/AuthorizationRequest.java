package com.company.scopery.modules.iam.authorization.domain.model;

import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;

import java.util.UUID;

public record AuthorizationRequest(
        UUID userId,
        UUID resourceId,
        String rightCode,
        String permissionCode,
        String actionCode) {

    public AuthorizationRequest(UUID userId, UUID resourceId, String rightCode) {
        this(userId, resourceId, normalize(rightCode), null, null);
    }

    public AuthorizationRequest(UUID userId, UUID resourceId, IamPermissionAction authority) {
        this(userId, resourceId, authority.legacyRightCode(), authority.permissionCode(), authority.actionCode());
    }

    public String effectiveActionCode() {
        return actionCode != null ? actionCode : rightCode;
    }

    public String authorityLabel() {
        if (permissionCode != null && actionCode != null) {
            return permissionCode + "." + actionCode;
        }
        return rightCode;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Right code must not be blank");
        }
        return value.trim().toUpperCase();
    }
}
