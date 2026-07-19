package com.company.scopery.modules.collaboration.agendaitem.application.action;
import com.company.scopery.modules.collaboration.agendaitem.application.command.UpdateAgendaItemCommand;
import com.company.scopery.modules.collaboration.agendaitem.application.response.MeetingAgendaItemResponse;
import com.company.scopery.modules.collaboration.agendaitem.domain.enums.AgendaItemStatus;
import com.company.scopery.modules.collaboration.agendaitem.domain.model.MeetingAgendaItemRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.collaboration.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateAgendaItemAction {
    private final MeetingAgendaItemRepository agenda; private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public UpdateAgendaItemAction(MeetingAgendaItemRepository agenda, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.agenda=agenda; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingAgendaItemResponse execute(UpdateAgendaItemCommand c) {
        authorization.requireAgendaManage(c.projectId());
        var item = agenda.findByIdAndMeetingId(c.agendaItemId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.agendaNotFound(c.agendaItemId()));
        if (c.title() == null || c.title().isBlank()) throw CollaborationExceptions.titleRequired();
        var st = CollaborationEnumParser.parseRequired(AgendaItemStatus.class, c.status(), "status");
        item = agenda.save(item.update(c.title().trim(), c.description(), c.ownerUserId(), st, c.sortOrder() == null ? item.sortOrder() : c.sortOrder(), c.timebox(), Boolean.TRUE.equals(c.clientVisible())));
        activityLogger.logSuccess(CollaborationEntityTypes.AGENDA_ITEM, item.id(), CollaborationActivityActions.AGENDA_UPDATED, "Agenda updated");
        return MeetingAgendaItemResponse.from(item);
    }
}
