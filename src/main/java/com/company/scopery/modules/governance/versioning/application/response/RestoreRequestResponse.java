package com.company.scopery.modules.governance.versioning.application.response;
import com.company.scopery.modules.governance.versioning.domain.model.RestoreRequest;
import java.util.UUID;
public record RestoreRequestResponse(UUID id, UUID restoreFromVersionRecordId, String status) {
    public static RestoreRequestResponse from(RestoreRequest r) { return new RestoreRequestResponse(r.id(), r.restoreFromVersionRecordId(), r.status()); }
}
