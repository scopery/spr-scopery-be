package com.company.scopery.modules.documenthub.version.application.command;

import java.util.UUID;

public record CreatePresignedUploadCommand(
        UUID projectId,
        UUID documentId,
        String fileName,
        String contentType,
        String changeNotes
) {}
