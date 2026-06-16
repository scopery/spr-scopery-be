package com.company.scopery.modules.knowledge.documenttype.application.command;

import java.util.UUID;

public record CreateDocumentTypeCommand(
        String code,
        String name,
        String description,
        String documentScope,
        UUID workspaceId) {}
