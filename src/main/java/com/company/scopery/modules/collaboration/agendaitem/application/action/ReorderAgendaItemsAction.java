package com.company.scopery.modules.collaboration.agendaitem.application.action;
import com.company.scopery.modules.collaboration.agendaitem.application.command.ReorderAgendaItemsCommand;
import com.company.scopery.modules.collaboration.agendaitem.application.response.MeetingAgendaItemResponse;
import com.company.scopery.modules.collaboration.agendaitem.domain.model.MeetingAgendaItemRepository;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Component
public class ReorderAgendaItemsAction {
    private final MeetingAgendaItemRepository agenda; private final CollaborationAuthorizationService authorization;
    public ReorderAgendaItemsAction(MeetingAgendaItemRepository agenda, CollaborationAuthorizationService authorization) {
        this.agenda=agenda; this.authorization=authorization;
    }
    @Transactional
    public List<MeetingAgendaItemResponse> execute(ReorderAgendaItemsCommand c) {
        authorization.requireAgendaManage(c.projectId());
        List<MeetingAgendaItemResponse> out = new ArrayList<>();
        for (int i = 0; i < c.orderedIds().size(); i++) {
            var id = c.orderedIds().get(i);
            var item = agenda.findByIdAndMeetingId(id, c.meetingId()).orElseThrow(() -> CollaborationExceptions.agendaNotFound(id));
            item = agenda.save(item.withSortOrder(i));
            out.add(MeetingAgendaItemResponse.from(item));
        }
        return out;
    }
}
