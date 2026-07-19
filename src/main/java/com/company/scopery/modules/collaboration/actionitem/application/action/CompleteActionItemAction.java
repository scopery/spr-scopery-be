package com.company.scopery.modules.collaboration.actionitem.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.actionitem.application.command.CompleteActionItemCommand;
import com.company.scopery.modules.collaboration.actionitem.application.response.MeetingActionItemResponse;
import com.company.scopery.modules.collaboration.actionitem.domain.model.MeetingActionItemRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CompleteActionItemAction {
    private final MeetingActionItemRepository actions; private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final CollaborationActivityLogger activityLogger;
    public CompleteActionItemAction(MeetingActionItemRepository actions, CollaborationAuthorizationService authorization, CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.actions=actions; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingActionItemResponse execute(CompleteActionItemCommand c) {
        authorization.requireActionComplete(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var a = actions.findByIdAndProjectId(c.actionItemId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.actionNotFound(c.actionItemId()));
        a = actions.save(a.complete(actor.id(), c.note()));
        activityLogger.logSuccess(CollaborationEntityTypes.ACTION_ITEM, a.id(), CollaborationActivityActions.ACTION_COMPLETED, "Action completed");
        return MeetingActionItemResponse.from(a);
    }
}
