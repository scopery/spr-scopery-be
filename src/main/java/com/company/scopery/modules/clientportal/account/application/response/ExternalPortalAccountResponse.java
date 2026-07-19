package com.company.scopery.modules.clientportal.account.application.response;
import com.company.scopery.modules.clientportal.account.domain.model.ExternalPortalAccount;
import java.time.Instant; import java.util.UUID;
public record ExternalPortalAccountResponse(UUID id, UUID workspaceId, UUID contactId, String email,
                                            String displayName, String status, Instant lastLoginAt,
                                            Instant activatedAt, Instant createdAt) {
    public static ExternalPortalAccountResponse from(ExternalPortalAccount a) {
        return new ExternalPortalAccountResponse(a.id(), a.workspaceId(), a.contactId(), a.email(),
                a.displayName(), a.status().name(), a.lastLoginAt(), a.activatedAt(), a.createdAt());
    }
}
