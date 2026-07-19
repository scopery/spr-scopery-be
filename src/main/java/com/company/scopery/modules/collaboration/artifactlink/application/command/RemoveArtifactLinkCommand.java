package com.company.scopery.modules.collaboration.artifactlink.application.command;
import java.util.UUID;
public record RemoveArtifactLinkCommand(UUID projectId, UUID meetingId, UUID linkId) {}
