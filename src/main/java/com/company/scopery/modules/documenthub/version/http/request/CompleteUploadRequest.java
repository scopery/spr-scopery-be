package com.company.scopery.modules.documenthub.version.http.request;

public record CompleteUploadRequest(
        String checksum,
        Long fileSizeBytes
) {}
