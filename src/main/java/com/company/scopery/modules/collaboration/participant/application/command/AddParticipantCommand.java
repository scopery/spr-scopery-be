package com.company.scopery.modules.collaboration.participant.application.command;
import java.util.UUID;
public record AddParticipantCommand(UUID projectId, UUID meetingId, String targetType, UUID targetId, String displayName, String email, String role, Boolean clientVisible) {}
