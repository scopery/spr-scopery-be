package com.company.scopery.modules.collaboration.actionitem.application.action;
import com.company.scopery.modules.collaboration.actionitem.application.command.UpdateActionItemCommand;
import com.company.scopery.modules.collaboration.actionitem.application.response.MeetingActionItemResponse;
import com.company.scopery.modules.collaboration.actionitem.domain.enums.ActionItemStatus;
import com.company.scopery.modules.collaboration.actionitem.domain.model.MeetingActionItemRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import com.company.scopery.modules.collaboration.shared.util.CollaborationEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateActionItemAction {
    private final MeetingActionItemRepository actions; private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public UpdateActionItemAction(MeetingActionItemRepository actions, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.actions=actions; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingActionItemResponse execute(UpdateActionItemCommand c) {
        authorization.requireActionManage(c.projectId());
        var a = actions.findByIdAndProjectId(c.actionItemId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.actionNotFound(c.actionItemId()));
        if (c.title() == null || c.title().isBlank()) throw CollaborationExceptions.titleRequired();
        var st = CollaborationEnumParser.parseRequired(ActionItemStatus.class, c.status(), "status");
        a = actions.save(a.update(c.title().trim(), c.description(), c.ownerType(), c.ownerId(), c.dueDate(), st, Boolean.TRUE.equals(c.clientVisible())));
        activityLogger.logSuccess(CollaborationEntityTypes.ACTION_ITEM, a.id(), CollaborationActivityActions.ACTION_UPDATED, "Action updated");
        return MeetingActionItemResponse.from(a);
    }
}
