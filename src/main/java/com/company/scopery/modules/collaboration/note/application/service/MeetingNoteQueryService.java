package com.company.scopery.modules.collaboration.note.application.service;
import com.company.scopery.modules.collaboration.note.application.response.MeetingNoteResponse;
import com.company.scopery.modules.collaboration.note.domain.model.MeetingNoteRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class MeetingNoteQueryService {
    private final MeetingNoteRepository notes; private final CollaborationAuthorizationService authorization;
    public MeetingNoteQueryService(MeetingNoteRepository notes, CollaborationAuthorizationService authorization) { this.notes=notes; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public List<MeetingNoteResponse> list(UUID projectId, UUID meetingId) {
        authorization.requireMeetingView(projectId);
        return notes.findByMeetingId(meetingId).stream().filter(n -> n.archivedAt() == null).map(MeetingNoteResponse::from).toList();
    }
}
