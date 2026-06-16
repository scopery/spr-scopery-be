package com.company.scopery.modules.iam.grant.application.query;

import java.util.UUID;

public record SearchIamAccessGrantQuery(
        UUID subjectId,
        UUID resourceId,
        String status,
        int page,
        int size) {
}
