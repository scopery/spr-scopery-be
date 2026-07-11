package com.company.scopery.modules.workspace.access.application.response;

import java.util.List;
import java.util.UUID;

public record WorkspaceAccessResponse(UUID workspaceId, List<WorkspaceSubjectAccessResponse> subjects) {
}
