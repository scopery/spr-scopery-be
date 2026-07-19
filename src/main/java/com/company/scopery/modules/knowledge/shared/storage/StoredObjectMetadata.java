package com.company.scopery.modules.knowledge.shared.storage;

public record StoredObjectMetadata(
        String objectKey,
        long sizeBytes,
        String contentType,
        String etag
) {}
