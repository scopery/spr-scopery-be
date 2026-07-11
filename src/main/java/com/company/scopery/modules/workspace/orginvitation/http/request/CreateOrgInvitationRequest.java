package com.company.scopery.modules.workspace.orginvitation.http.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record CreateOrgInvitationRequest(
        @NotBlank @Email String inviteeEmail,
        String membershipType,
        Instant expiresAt) {
}
