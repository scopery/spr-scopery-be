package com.company.scopery.modules.documenthub.share.application.command;
import java.time.Instant; import java.util.UUID;
public record CreateDocumentShareCommand(UUID projectId, UUID documentId, String shareType, String granteeType, UUID granteeId, Instant expiresAt) {}
