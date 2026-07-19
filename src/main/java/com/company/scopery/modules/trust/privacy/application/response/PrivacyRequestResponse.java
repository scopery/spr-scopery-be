package com.company.scopery.modules.trust.privacy.application.response;
import com.company.scopery.modules.trust.privacy.domain.model.PrivacyRequest;
import java.util.UUID;
public record PrivacyRequestResponse(UUID id, UUID workspaceId, String requestCode, String requestType, String status, String subjectReference) {
    public static PrivacyRequestResponse from(PrivacyRequest r) {
        return new PrivacyRequestResponse(r.id(), r.workspaceId(), r.requestCode(), r.requestType(), r.status(), r.subjectReference());
    }
}
