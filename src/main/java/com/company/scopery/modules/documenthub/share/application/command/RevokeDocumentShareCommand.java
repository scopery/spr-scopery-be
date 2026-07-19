package com.company.scopery.modules.documenthub.share.application.command;

import java.util.UUID;

public record RevokeDocumentShareCommand(UUID projectId, UUID shareId) {}
