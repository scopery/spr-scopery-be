package com.company.scopery.modules.workspace.orginvitation.application.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Pending organization invitation for the current user")
public record MyOrgInvitationResponse(
        UUID id,
        UUID organizationId,
        String organizationName,
        String inviteeEmail,
        String membershipType,
        String status,
        Instant expiresAt,
        Instant createdAt
) {}
