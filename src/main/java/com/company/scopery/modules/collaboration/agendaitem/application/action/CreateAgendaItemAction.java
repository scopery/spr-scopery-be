package com.company.scopery.modules.collaboration.agendaitem.application.action;
import com.company.scopery.modules.collaboration.agendaitem.application.command.CreateAgendaItemCommand;
import com.company.scopery.modules.collaboration.agendaitem.application.response.MeetingAgendaItemResponse;
import com.company.scopery.modules.collaboration.agendaitem.domain.model.*;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateAgendaItemAction {
    private final MeetingRepository meetings; private final MeetingAgendaItemRepository agenda;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public CreateAgendaItemAction(MeetingRepository meetings, MeetingAgendaItemRepository agenda,
                                  CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.meetings=meetings; this.agenda=agenda; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingAgendaItemResponse execute(CreateAgendaItemCommand c) {
        authorization.requireAgendaManage(c.projectId());
        var meeting = meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        if (c.title() == null || c.title().isBlank()) throw CollaborationExceptions.titleRequired();
        int order = c.sortOrder() == null ? agenda.findByMeetingId(c.meetingId()).size() : c.sortOrder();
        var item = MeetingAgendaItem.create(meeting.workspaceId(), meeting.projectId(), meeting.id(), c.title().trim(), c.description(), c.ownerUserId(), order, c.timebox(), Boolean.TRUE.equals(c.clientVisible()));
        item = agenda.save(item);
        activityLogger.logSuccess(CollaborationEntityTypes.AGENDA_ITEM, item.id(), CollaborationActivityActions.AGENDA_CREATED, "Agenda created");
        return MeetingAgendaItemResponse.from(item);
    }
}
