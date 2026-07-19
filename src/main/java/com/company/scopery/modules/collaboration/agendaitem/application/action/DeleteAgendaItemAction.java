package com.company.scopery.modules.collaboration.agendaitem.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.agendaitem.application.command.DeleteAgendaItemCommand;
import com.company.scopery.modules.collaboration.agendaitem.domain.model.MeetingAgendaItemRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class DeleteAgendaItemAction {
    private final MeetingAgendaItemRepository agenda; private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final CollaborationActivityLogger activityLogger;
    public DeleteAgendaItemAction(MeetingAgendaItemRepository agenda, CollaborationAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.agenda=agenda; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public void execute(DeleteAgendaItemCommand c) {
        authorization.requireAgendaManage(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var item = agenda.findByIdAndMeetingId(c.agendaItemId(), c.meetingId()).orElseThrow(() -> CollaborationExceptions.agendaNotFound(c.agendaItemId()));
        agenda.save(item.archive(actor.id()));
        activityLogger.logSuccess(CollaborationEntityTypes.AGENDA_ITEM, c.agendaItemId(), CollaborationActivityActions.AGENDA_ARCHIVED, "Agenda archived");
    }
}
