package com.company.scopery.modules.collaboration.agendaitem.application.service;
import com.company.scopery.modules.collaboration.agendaitem.application.response.MeetingAgendaItemResponse;
import com.company.scopery.modules.collaboration.agendaitem.domain.model.MeetingAgendaItemRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class MeetingAgendaItemQueryService {
    private final MeetingAgendaItemRepository agenda; private final CollaborationAuthorizationService authorization;
    public MeetingAgendaItemQueryService(MeetingAgendaItemRepository agenda, CollaborationAuthorizationService authorization) {
        this.agenda=agenda; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<MeetingAgendaItemResponse> list(UUID projectId, UUID meetingId) {
        authorization.requireMeetingView(projectId);
        return agenda.findByMeetingId(meetingId).stream().filter(a -> a.archivedAt() == null).map(MeetingAgendaItemResponse::from).toList();
    }
}
