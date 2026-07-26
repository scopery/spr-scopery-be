package com.company.scopery.modules.documenthub.documentlink.application.command;

import com.company.scopery.modules.documenthub.documentlink.domain.enums.DocumentLinkEntityType;
import com.company.scopery.modules.documenthub.documentlink.domain.enums.DocumentLinkRelationType;

import java.util.List;
import java.util.UUID;

public record BulkCreateDocumentLinksCommand(
        UUID workspaceId,
        UUID projectId,
        DocumentLinkEntityType linkedEntityType,
        UUID linkedEntityId,
        DocumentLinkRelationType relationType,
        List<UUID> documentIds
) {}
