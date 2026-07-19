package com.company.scopery.modules.collaboration.participant.application.command;
import java.util.UUID;
public record RemoveParticipantCommand(UUID projectId, UUID meetingId, UUID participantId) {}
