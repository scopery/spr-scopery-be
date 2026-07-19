package com.company.scopery.modules.integrationhub.importjob.domain.model;
import java.time.Instant; import java.util.UUID;
public record ImportRowResult(UUID id, UUID workspaceId, UUID importJobId, long rowNumber, String rowReference,
        String status, String message, String validationErrorsJson,
        String targetObjectType, UUID targetObjectId, String externalId,
        int version, Instant createdAt) {

    public static ImportRowResult valid(UUID workspaceId, UUID jobId, long rowNumber) {
        return new ImportRowResult(UUID.randomUUID(), workspaceId, jobId, rowNumber, null,
                "VALID", null, null, null, null, null, 0, Instant.now());
    }
    public static ImportRowResult invalid(UUID workspaceId, UUID jobId, long rowNumber, String message, String errorsJson) {
        return new ImportRowResult(UUID.randomUUID(), workspaceId, jobId, rowNumber, null,
                "INVALID", message, errorsJson, null, null, null, 0, Instant.now());
    }
    public static ImportRowResult failed(UUID workspaceId, UUID jobId, long rowNumber, String message) {
        return new ImportRowResult(UUID.randomUUID(), workspaceId, jobId, rowNumber, null,
                "FAILED", message, null, null, null, null, 0, Instant.now());
    }
    public static ImportRowResult created(UUID workspaceId, UUID jobId, long rowNumber, UUID targetId, String targetType) {
        return new ImportRowResult(UUID.randomUUID(), workspaceId, jobId, rowNumber, null,
                "CREATED", null, null, targetType, targetId, null, 0, Instant.now());
    }
}
