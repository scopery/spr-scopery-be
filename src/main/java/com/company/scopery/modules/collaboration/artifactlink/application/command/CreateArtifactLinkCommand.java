package com.company.scopery.modules.collaboration.artifactlink.application.command;
import java.util.UUID;
public record CreateArtifactLinkCommand(UUID projectId, UUID meetingId, UUID agendaItemId, UUID noteId, UUID actionItemId, String targetType, UUID targetId, String linkType) {}
