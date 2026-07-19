package com.company.scopery.modules.collaboration.actionitem.application.action;
import com.company.scopery.modules.collaboration.actionitem.application.command.CreateActionItemCommand;
import com.company.scopery.modules.collaboration.actionitem.application.response.MeetingActionItemResponse;
import com.company.scopery.modules.collaboration.actionitem.domain.model.*;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateActionItemAction {
    private final MeetingRepository meetings; private final MeetingActionItemRepository actions;
    private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public CreateActionItemAction(MeetingRepository meetings, MeetingActionItemRepository actions, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.meetings=meetings; this.actions=actions; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingActionItemResponse execute(CreateActionItemCommand c) {
        authorization.requireActionManage(c.projectId());
        var meeting = meetings.findByIdAndProjectId(c.meetingId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.meetingNotFound(c.meetingId()));
        if (c.title() == null || c.title().isBlank()) throw CollaborationExceptions.titleRequired();
        var a = MeetingActionItem.create(meeting.workspaceId(), meeting.projectId(), meeting.id(), c.agendaItemId(), c.title().trim(), c.description(), c.ownerType(), c.ownerId(), c.dueDate(), Boolean.TRUE.equals(c.clientVisible()));
        a = actions.save(a);
        activityLogger.logSuccess(CollaborationEntityTypes.ACTION_ITEM, a.id(), CollaborationActivityActions.ACTION_CREATED, "Action created");
        return MeetingActionItemResponse.from(a);
    }
}
