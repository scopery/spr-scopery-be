package com.company.scopery.platform.storage;

import java.util.UUID;

/**
 * Defines where a category of objects lives in object storage.
 * Build object keys via {@link #keyFor} — never construct path strings manually.
 *
 * <p>Default max upload size is 5 MB. Override with {@link #of(String, String, String, long)}.
 *
 * <p>Usage:
 * <pre>
 *   String key = TraceabilityStorageLocations.SCREEN_MOCKUP.keyFor(screenId, filename);
 *   storage.createPresignedUpload(key, contentType, location.maxSizeBytes());
 * </pre>
 */
public record StorageLocation(String module, String entityType, String purpose, long maxSizeBytes) {

    public static final long MB = 1024L * 1024L;
    private static final long DEFAULT_MAX_BYTES = 5L * MB;

    public static StorageLocation of(String module, String entityType, String purpose) {
        return new StorageLocation(module, entityType, purpose, DEFAULT_MAX_BYTES);
    }

    public static StorageLocation of(String module, String entityType, String purpose, long maxSizeBytes) {
        return new StorageLocation(module, entityType, purpose, maxSizeBytes);
    }

    /** Full object key for a file belonging to a specific entity instance. */
    public String keyFor(UUID entityId, String filename) {
        return module + "/" + entityType + "/" + entityId + "/" + purpose + "/" + filename;
    }

    /** Prefix covering all objects for a specific entity instance — use to list or bulk-delete. */
    public String prefixFor(UUID entityId) {
        return module + "/" + entityType + "/" + entityId + "/" + purpose + "/";
    }

    /** Prefix covering all objects for the entire module — use for auditing or lifecycle policies. */
    public String modulePrefix() {
        return module + "/";
    }
}
