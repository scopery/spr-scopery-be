package com.company.scopery.modules.externalparty.channel.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for adding a communication channel to an external contact")
public record CreateExternalContactChannelRequest(
        @Schema(description = "Type of the communication channel", example = "EMAIL", allowableValues = {"EMAIL", "PHONE", "LINKEDIN", "SLACK", "TEAMS", "WHATSAPP"})
        @NotBlank String channelType,

        @Schema(description = "The channel address or handle (e.g. email address, phone number, Slack handle)", example = "jane.doe@acme.com")
        @NotBlank String channelValue,

        @Schema(description = "Whether this is the primary channel for the contact", example = "true")
        boolean primaryFlag) {}
