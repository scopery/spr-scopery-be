package com.company.scopery.modules.externalparty.preference.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for creating or updating a communication preference for an external party")
public record UpsertCommunicationPreferenceRequest(
        @Schema(description = "Preferred communication channel type", example = "EMAIL", allowableValues = {"EMAIL", "PHONE", "SLACK", "TEAMS", "WHATSAPP"}, nullable = true)
        String preferredChannelType,

        @Schema(description = "Preferred language for communications (ISO 639-1 code)", example = "en", nullable = true)
        String preferredLanguage,

        @Schema(description = "Whether the external party should not be contacted", example = "false")
        boolean doNotContact,

        @Schema(description = "Additional notes about communication preferences", example = "Prefers morning calls only", nullable = true)
        String notes) {}
