package com.company.scopery.modules.collaboration.actionitem.application.service;
import com.company.scopery.modules.collaboration.actionitem.application.response.MeetingActionItemResponse;
import com.company.scopery.modules.collaboration.actionitem.domain.model.MeetingActionItemRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class MeetingActionItemQueryService {
    private final MeetingActionItemRepository actions; private final CollaborationAuthorizationService authorization;
    public MeetingActionItemQueryService(MeetingActionItemRepository actions, CollaborationAuthorizationService authorization) { this.actions=actions; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public MeetingActionItemResponse get(UUID projectId, UUID actionItemId) {
        authorization.requireMeetingView(projectId);
        return MeetingActionItemResponse.from(actions.findByIdAndProjectId(actionItemId, projectId).orElseThrow(() -> CollaborationExceptions.actionNotFound(actionItemId)));
    }
    @Transactional(readOnly=true)
    public List<MeetingActionItemResponse> listByMeeting(UUID projectId, UUID meetingId) {
        authorization.requireMeetingView(projectId);
        return actions.findByMeetingId(meetingId).stream().filter(a -> a.archivedAt() == null).map(MeetingActionItemResponse::from).toList();
    }
}
