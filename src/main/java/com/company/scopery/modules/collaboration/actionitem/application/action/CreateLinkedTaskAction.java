package com.company.scopery.modules.collaboration.actionitem.application.action;
import com.company.scopery.modules.collaboration.actionitem.application.command.CreateLinkedTaskCommand;
import com.company.scopery.modules.collaboration.actionitem.application.response.MeetingActionItemResponse;
import com.company.scopery.modules.collaboration.actionitem.domain.model.MeetingActionItemRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateLinkedTaskAction {
    private final MeetingActionItemRepository actions; private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public CreateLinkedTaskAction(MeetingActionItemRepository actions, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.actions=actions; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    /** Links an existing task id explicitly; does not invent task access grants. */
    @Transactional
    public MeetingActionItemResponse execute(CreateLinkedTaskCommand c) {
        authorization.requireActionCreateTask(c.projectId());
        var a = actions.findByIdAndProjectId(c.actionItemId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.actionNotFound(c.actionItemId()));
        try { a = a.withLinkedTask(c.taskId()); }
        catch (IllegalStateException ex) { throw CollaborationExceptions.actionAlreadyLinked(c.actionItemId()); }
        a = actions.save(a);
        activityLogger.logSuccess(CollaborationEntityTypes.ACTION_ITEM, a.id(), CollaborationActivityActions.ACTION_LINKED_TASK, "Linked task " + c.taskId());
        return MeetingActionItemResponse.from(a);
    }
}
