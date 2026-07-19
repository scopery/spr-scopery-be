package com.company.scopery.modules.clientportal.grant.http.request;
import jakarta.validation.constraints.NotNull;
import java.time.Instant; import java.util.UUID;
public record CreatePortalAccessGrantRequest(@NotNull UUID portalAccountId, String permissionPolicyCode, Instant expiresAt) {}
