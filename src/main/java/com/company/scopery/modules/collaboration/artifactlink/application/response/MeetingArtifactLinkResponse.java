package com.company.scopery.modules.collaboration.artifactlink.application.response;
import com.company.scopery.modules.collaboration.artifactlink.domain.model.MeetingArtifactLink;
import java.util.UUID;
public record MeetingArtifactLinkResponse(UUID id, UUID meetingId, String targetType, UUID targetId, String linkType) {
    public static MeetingArtifactLinkResponse from(MeetingArtifactLink l) { return new MeetingArtifactLinkResponse(l.id(), l.meetingId(), l.targetType(), l.targetId(), l.linkType().name()); }
}
