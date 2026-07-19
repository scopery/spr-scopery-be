package com.company.scopery.modules.integrationhub.importjob.application.response;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportRowResult;
import java.time.Instant; import java.util.UUID;
public record ImportRowResultResponse(UUID id, UUID importJobId, long rowNumber, String status, String message,
        String targetObjectType, UUID targetObjectId, String externalId, Instant createdAt) {
    public static ImportRowResultResponse from(ImportRowResult r) {
        return new ImportRowResultResponse(r.id(), r.importJobId(), r.rowNumber(), r.status(), r.message(),
                r.targetObjectType(), r.targetObjectId(), r.externalId(), r.createdAt());
    }
}
