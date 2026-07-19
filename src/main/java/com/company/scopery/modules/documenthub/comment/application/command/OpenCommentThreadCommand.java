package com.company.scopery.modules.documenthub.comment.application.command;

import java.util.UUID;

public record OpenCommentThreadCommand(
        UUID projectId,
        UUID documentId,
        String blockId,
        String anchorText,
        String firstCommentBody
) {}
