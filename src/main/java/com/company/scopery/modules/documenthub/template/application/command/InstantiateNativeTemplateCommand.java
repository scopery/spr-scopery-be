package com.company.scopery.modules.documenthub.template.application.command;

import java.util.Map;
import java.util.UUID;

public record InstantiateNativeTemplateCommand(
        UUID workspaceId,
        UUID templateId,
        UUID templateVersionId,
        UUID projectId,
        UUID targetDocumentId,
        Map<String, String> variables
) {}
