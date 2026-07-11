package com.company.scopery.modules.iam.access.application.query;

public record ExplainAccessQuery(
        String permissionCode, String actionCode, String resourceType, String resourceRefId) {
}
