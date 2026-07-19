package com.company.scopery.modules.collaboration.artifactlink.application.service;
import com.company.scopery.modules.collaboration.artifactlink.application.response.MeetingArtifactLinkResponse;
import com.company.scopery.modules.collaboration.artifactlink.domain.model.MeetingArtifactLinkRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class MeetingArtifactLinkQueryService {
    private final MeetingArtifactLinkRepository links; private final CollaborationAuthorizationService authorization;
    public MeetingArtifactLinkQueryService(MeetingArtifactLinkRepository links, CollaborationAuthorizationService authorization) { this.links=links; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public List<MeetingArtifactLinkResponse> list(UUID projectId, UUID meetingId) {
        authorization.requireMeetingView(projectId);
        return links.findByMeetingId(meetingId).stream().map(MeetingArtifactLinkResponse::from).toList();
    }
}
