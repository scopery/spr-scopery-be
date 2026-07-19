package com.company.scopery.modules.collaboration.participant.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdateParticipantRequest(@NotBlank String participantRole, @NotBlank String attendanceStatus, Boolean clientVisible) {}
