package com.company.scopery.modules.knowledge.documenttypefield.application.command;

import java.util.List;
import java.util.UUID;

public record ReorderDocumentTypeFieldsCommand(UUID documentTypeId, List<UUID> orderedFieldIds) {}
