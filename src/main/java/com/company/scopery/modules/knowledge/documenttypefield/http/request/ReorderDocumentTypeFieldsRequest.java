package com.company.scopery.modules.knowledge.documenttypefield.http.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderDocumentTypeFieldsRequest(@NotEmpty List<UUID> orderedFieldIds) {}
