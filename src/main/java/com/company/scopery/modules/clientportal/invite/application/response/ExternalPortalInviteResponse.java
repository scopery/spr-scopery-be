package com.company.scopery.modules.clientportal.invite.application.response;
import com.company.scopery.modules.clientportal.invite.domain.model.ExternalPortalInvite;
import java.time.Instant; import java.util.UUID;
public record ExternalPortalInviteResponse(UUID id, UUID projectId, String email, String status, Instant expiresAt, String inviteToken, Instant createdAt) {
    public static ExternalPortalInviteResponse from(ExternalPortalInvite e, String rawToken) {
        return new ExternalPortalInviteResponse(e.id(), e.projectId(), e.email(), e.status().name(), e.expiresAt(), rawToken, e.createdAt());
    }
    public static ExternalPortalInviteResponse from(ExternalPortalInvite e) {
        return from(e, null);
    }
}
