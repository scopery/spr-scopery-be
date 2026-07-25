package com.company.scopery.modules.documenthub.document.application.command;

import java.util.UUID;

public record UpdateDocumentCommand(UUID projectId, UUID documentId, String title) {}
