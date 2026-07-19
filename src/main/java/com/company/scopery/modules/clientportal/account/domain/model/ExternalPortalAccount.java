package com.company.scopery.modules.clientportal.account.domain.model;
import com.company.scopery.modules.clientportal.account.domain.enums.PortalAccountStatus;
import java.time.Instant; import java.util.UUID;
public record ExternalPortalAccount(UUID id, UUID workspaceId, UUID contactId, String email, String displayName, PortalAccountStatus status,
                                    String passwordHash, Instant lastLoginAt, Instant activatedAt, int version, Instant createdAt, Instant updatedAt) {
    public static ExternalPortalAccount createInvited(UUID workspaceId, UUID contactId, String email, String displayName) {
        Instant now = Instant.now();
        return new ExternalPortalAccount(UUID.randomUUID(), workspaceId, contactId, email.toLowerCase(), displayName, PortalAccountStatus.INVITED, null, null, null, 0, now, now);
    }
    public ExternalPortalAccount activate(String passwordHash) {
        Instant now = Instant.now();
        return new ExternalPortalAccount(id, workspaceId, contactId, email, displayName, PortalAccountStatus.ACTIVE, passwordHash, null, now, version, createdAt, now);
    }
    public ExternalPortalAccount recordLogin() {
        Instant now = Instant.now();
        return new ExternalPortalAccount(id, workspaceId, contactId, email, displayName, status, passwordHash, now, activatedAt, version, createdAt, now);
    }
    public ExternalPortalAccount changePassword(String passwordHash) {
        return new ExternalPortalAccount(id, workspaceId, contactId, email, displayName, status, passwordHash, lastLoginAt, activatedAt, version, createdAt, Instant.now());
    }
    public ExternalPortalAccount suspend() {
        if (status != PortalAccountStatus.ACTIVE) throw new IllegalStateException("Only ACTIVE accounts can be suspended");
        return new ExternalPortalAccount(id, workspaceId, contactId, email, displayName, PortalAccountStatus.SUSPENDED, passwordHash, lastLoginAt, activatedAt, version, createdAt, Instant.now());
    }
    public ExternalPortalAccount deactivate() {
        if (status == PortalAccountStatus.DEACTIVATED) throw new IllegalStateException("Account is already deactivated");
        return new ExternalPortalAccount(id, workspaceId, contactId, email, displayName, PortalAccountStatus.DEACTIVATED, passwordHash, lastLoginAt, activatedAt, version, createdAt, Instant.now());
    }
}
