package com.company.scopery.modules.collaboration.participant.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record AddParticipantRequest(@NotBlank String targetType, UUID targetId, String displayName, String email,
        @NotBlank String participantRole, Boolean clientVisible) {}
