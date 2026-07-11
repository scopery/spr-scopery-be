package com.company.scopery.modules.iam.authorization.application.query;

public record CheckAuthorizationQuery(
        String permissionCode, String actionCode, String resourceType, String resourceRefId) {
}
