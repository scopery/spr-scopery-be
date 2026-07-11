package com.company.scopery.modules.iam.grant.http.request;

import java.util.UUID;

public record SearchIamAccessGrantRequest(
        UUID subjectId, UUID resourceId, UUID workspaceId, String status, int page, int size) {
    public SearchIamAccessGrantRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
