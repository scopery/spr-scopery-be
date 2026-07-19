package com.company.scopery.modules.collaboration.participant.application.command;
import java.util.UUID;
public record MarkParticipantAttendedCommand(UUID projectId, UUID meetingId, UUID participantId) {}
