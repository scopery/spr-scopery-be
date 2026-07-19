package com.company.scopery.modules.collaboration.participant.application.command;
import java.util.UUID;
public record UpdateParticipantCommand(UUID projectId, UUID meetingId, UUID participantId, String role, String attendance, Boolean clientVisible) {}
