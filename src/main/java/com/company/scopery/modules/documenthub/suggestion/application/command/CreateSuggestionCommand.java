package com.company.scopery.modules.documenthub.suggestion.application.command;

import java.util.List;
import java.util.UUID;

public record CreateSuggestionCommand(
        UUID projectId,
        UUID documentId,
        long targetRevisionNo,
        String description,
        List<OperationItem> operations
) {
    public record OperationItem(String opType, String blockId, String path, String value, int ordinal) {}
}
