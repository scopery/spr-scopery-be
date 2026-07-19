package com.company.scopery.modules.clientportal.auth.application.response;
import java.time.Instant; import java.util.UUID;
public record PortalMeResponse(UUID id, UUID workspaceId, String email, String displayName, String status, Instant lastLoginAt) {}
