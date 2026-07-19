package com.company.scopery.modules.clientportal.auth.application.response;
import java.util.UUID;
public record PortalAuthResult(UUID portalAccountId, String email, String displayName, String accessToken, long expiresInMs) {}
