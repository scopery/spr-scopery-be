package com.company.scopery.modules.knowledge.shared.storage;

public interface ObjectStorageProvider {

    PresignedUpload createPresignedUpload(String objectKey, String contentType, long maxSizeBytes);

    PresignedDownload createPresignedDownload(String objectKey, String contentDispositionFilename);

    StoredObjectMetadata head(String objectKey);

    void delete(String objectKey);
}
