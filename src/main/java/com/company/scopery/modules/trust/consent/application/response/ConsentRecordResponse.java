package com.company.scopery.modules.trust.consent.application.response;
import com.company.scopery.modules.trust.consent.domain.model.ConsentRecord;
import java.time.Instant; import java.util.UUID;
public record ConsentRecordResponse(UUID id, UUID workspaceId, String consentType, String status,
        Instant givenAt, Instant withdrawnAt, Instant createdAt) {
    public static ConsentRecordResponse from(ConsentRecord r) {
        return new ConsentRecordResponse(r.id(), r.workspaceId(), r.consentType(), r.status(),
                r.givenAt(), r.withdrawnAt(), r.createdAt());
    }
}
