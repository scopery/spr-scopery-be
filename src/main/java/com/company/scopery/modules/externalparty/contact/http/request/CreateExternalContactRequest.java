package com.company.scopery.modules.externalparty.contact.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Request payload for creating a new external contact")
public record CreateExternalContactRequest(
        @Schema(description = "ID of the external organization this contact belongs to", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID organizationId,

        @Schema(description = "First name of the external contact", example = "Jane")
        @NotBlank String firstName,

        @Schema(description = "Last name of the external contact", example = "Doe")
        @NotBlank String lastName,

        @Schema(description = "Email address of the external contact", example = "jane.doe@acme.com", nullable = true)
        String email,

        @Schema(description = "Phone number of the external contact", example = "+1-555-0100", nullable = true)
        String phone,

        @Schema(description = "Job title of the external contact", example = "Chief Technology Officer", nullable = true)
        String title,

        @Schema(description = "Whether this contact is the primary contact for the organization", example = "true", nullable = true)
        Boolean primaryFlag) {}
