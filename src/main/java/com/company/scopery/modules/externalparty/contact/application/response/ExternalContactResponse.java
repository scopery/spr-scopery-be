package com.company.scopery.modules.externalparty.contact.application.response;

import com.company.scopery.modules.externalparty.contact.domain.model.ExternalContact;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of an external contact")
public record ExternalContactResponse(
        @Schema(description = "Unique identifier of the external contact", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the workspace this contact belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "ID of the external organization this contact is associated with", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID organizationId,

        @Schema(description = "First name of the external contact", example = "Jane")
        String firstName,

        @Schema(description = "Last name of the external contact", example = "Doe")
        String lastName,

        @Schema(description = "Email address of the external contact", example = "jane.doe@acme.com", nullable = true)
        String email,

        @Schema(description = "Current status of the external contact", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,

        @Schema(description = "Whether this is the primary contact for the organization", example = "true")
        boolean primaryFlag,

        @Schema(description = "Timestamp when the contact was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt) {

    public static ExternalContactResponse from(ExternalContact e) {
        return new ExternalContactResponse(e.id(), e.workspaceId(), e.organizationId(), e.firstName(), e.lastName(), e.email(), e.status().name(), e.primaryFlag(), e.createdAt());
    }
}
