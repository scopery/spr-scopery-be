package com.company.scopery.modules.documenthub.document.application.command;

import java.util.UUID;

public record ApproveDocumentCommand(UUID projectId, UUID documentId) {}
