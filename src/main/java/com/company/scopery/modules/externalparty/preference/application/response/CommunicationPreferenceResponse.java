package com.company.scopery.modules.externalparty.preference.application.response;

import com.company.scopery.modules.externalparty.preference.domain.model.CommunicationPreference;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a communication preference for an external organization or contact")
public record CommunicationPreferenceResponse(
        @Schema(description = "Unique identifier of the preference record", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID id,

        @Schema(description = "ID of the workspace this preference belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "ID of the external organization this preference applies to", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID externalOrganizationId,

        @Schema(description = "ID of the external contact this preference applies to", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID externalContactId,

        @Schema(description = "Preferred communication channel type", example = "EMAIL", allowableValues = {"EMAIL", "PHONE", "SLACK", "TEAMS", "WHATSAPP"}, nullable = true)
        String preferredChannelType,

        @Schema(description = "Preferred language for communications (ISO 639-1 code)", example = "en", nullable = true)
        String preferredLanguage,

        @Schema(description = "Whether the external party should not be contacted", example = "false")
        boolean doNotContact,

        @Schema(description = "Additional notes about communication preferences", example = "Prefers morning calls only", nullable = true)
        String notes,

        @Schema(description = "Timestamp when the preference was created", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant createdAt,

        @Schema(description = "Timestamp when the preference was last updated", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant updatedAt) {

    public static CommunicationPreferenceResponse from(CommunicationPreference p) {
        return new CommunicationPreferenceResponse(p.id(), p.workspaceId(),
                p.externalOrganizationId(), p.externalContactId(),
                p.preferredChannelType(), p.preferredLanguage(),
                p.doNotContact(), p.notes(), p.createdAt(), p.updatedAt());
    }

    public static CommunicationPreferenceResponse empty(UUID workspaceId, UUID organizationId, UUID contactId) {
        return new CommunicationPreferenceResponse(null, workspaceId, organizationId, contactId,
                null, null, false, null, null, null);
    }
}
