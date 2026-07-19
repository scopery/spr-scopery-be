package com.company.scopery.modules.externalparty.channel.application.response;

import com.company.scopery.modules.externalparty.channel.domain.model.ExternalContactChannel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a communication channel for an external contact")
public record ExternalContactChannelResponse(
        @Schema(description = "Unique identifier of the channel record", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the workspace this channel belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "ID of the external contact this channel belongs to", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID externalContactId,

        @Schema(description = "Type of the communication channel", example = "EMAIL", allowableValues = {"EMAIL", "PHONE", "LINKEDIN", "SLACK", "TEAMS", "WHATSAPP"}, nullable = true)
        String channelType,

        @Schema(description = "The channel address or handle", example = "jane.doe@acme.com")
        String channelValue,

        @Schema(description = "Whether this is the primary channel for the contact", example = "true")
        boolean primaryFlag,

        @Schema(description = "Timestamp when the channel was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the channel was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static ExternalContactChannelResponse from(ExternalContactChannel c) {
        return new ExternalContactChannelResponse(c.id(), c.workspaceId(), c.externalContactId(),
                c.channelType() != null ? c.channelType().name() : null,
                c.channelValue(), c.primaryFlag(), c.createdAt(), c.updatedAt());
    }
}
