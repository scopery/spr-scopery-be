package com.company.scopery.modules.productivity.workinbox.application.action;
import com.company.scopery.modules.productivity.shared.activity.ProductivityActivityLogger;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.shared.constant.*;
import com.company.scopery.modules.productivity.shared.error.ProductivityExceptions;
import com.company.scopery.modules.productivity.workinbox.application.response.WorkInboxItemResponse;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItemRepository;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.productivity.workinbox.application.command.MarkInboxReadCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class MarkInboxReadAction {
    private final WorkInboxItemRepository repo; private final ProductivityAuthorizationService authorization; private final ProductivityActivityLogger activityLogger;
    public MarkInboxReadAction(WorkInboxItemRepository repo, ProductivityAuthorizationService authorization, ProductivityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public WorkInboxItemResponse execute(MarkInboxReadCommand c) {
        authorization.requireInboxView(c.workspaceId());
        var item = repo.findByIdAndWorkspaceId(c.itemId(), c.workspaceId()).orElseThrow(() -> ProductivityExceptions.inboxNotFound(c.itemId()));
        item = repo.save(item.markRead());
        activityLogger.logSuccess(ProductivityEntityTypes.WORK_INBOX, item.id(), ProductivityActivityActions.INBOX_READ, "Inbox read");
        return WorkInboxItemResponse.from(item);
    }
}
