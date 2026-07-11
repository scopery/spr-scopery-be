package com.company.scopery.modules.workspace.access.application.response;

import java.util.List;
import java.util.UUID;

public record WorkspaceSubjectAccessResponse(
        String subjectType,
        UUID subjectId,
        boolean allowed,
        List<String> accessSources,
        List<UUID> contributingGrantIds) {
}
