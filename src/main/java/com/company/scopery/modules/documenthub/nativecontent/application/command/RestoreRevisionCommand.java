package com.company.scopery.modules.documenthub.nativecontent.application.command;

import java.util.UUID;

public record RestoreRevisionCommand(
        UUID projectId,
        UUID documentId,
        long revisionNo
) {}
