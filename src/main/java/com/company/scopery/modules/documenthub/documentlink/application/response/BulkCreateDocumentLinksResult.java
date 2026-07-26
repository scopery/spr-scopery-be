package com.company.scopery.modules.documenthub.documentlink.application.response;

import java.util.List;
import java.util.UUID;

public record BulkCreateDocumentLinksResult(
        int createdCount,
        int skippedDuplicateCount,
        int failedCount,
        List<DocumentLinkResponse> createdLinks,
        List<UUID> skippedDocuments
) {}
