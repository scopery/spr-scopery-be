package com.company.scopery.modules.collaboration.artifactlink.domain.model;
import com.company.scopery.modules.collaboration.artifactlink.domain.enums.ArtifactLinkType;
import java.time.Instant; import java.util.UUID;
public record MeetingArtifactLink(
        UUID id, UUID workspaceId, UUID projectId, UUID meetingId, UUID agendaItemId, UUID noteId, UUID actionItemId,
        String targetType, UUID targetId, ArtifactLinkType linkType,
        Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt
) {
    public static MeetingArtifactLink create(UUID workspaceId, UUID projectId, UUID meetingId, UUID agendaItemId,
                                             UUID noteId, UUID actionItemId, String targetType, UUID targetId, ArtifactLinkType linkType) {
        Instant now = Instant.now();
        return new MeetingArtifactLink(UUID.randomUUID(), workspaceId, projectId, meetingId, agendaItemId, noteId,
                actionItemId, targetType, targetId, linkType, null, null, 0, now, now);
    }
    public MeetingArtifactLink archive(UUID actorId) {
        Instant now = Instant.now();
        return new MeetingArtifactLink(id, workspaceId, projectId, meetingId, agendaItemId, noteId, actionItemId,
                targetType, targetId, linkType, now, actorId, version, createdAt, now);
    }
}
