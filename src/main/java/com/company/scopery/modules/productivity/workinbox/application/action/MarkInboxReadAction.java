package com.company.scopery.modules.productivity.workinbox.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.shared.activity.ProductivityActivityLogger;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.shared.constant.ProductivityActivityActions;
import com.company.scopery.modules.productivity.shared.constant.ProductivityEntityTypes;
import com.company.scopery.modules.productivity.shared.error.ProductivityExceptions;
import com.company.scopery.modules.productivity.workinbox.application.command.MarkInboxReadCommand;
import com.company.scopery.modules.productivity.workinbox.application.response.WorkInboxItemResponse;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItem;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItemRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class MarkInboxReadAction {
    private final WorkInboxItemRepository repo;
    private final ProductivityAuthorizationService authorization;
    private final ProductivityActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUser;

    public MarkInboxReadAction(WorkInboxItemRepository repo,
                                ProductivityAuthorizationService authorization,
                                ProductivityActivityLogger activityLogger,
                                CurrentUserAuthorizationService currentUser) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.currentUser = currentUser;
    }

    @Transactional
    public WorkInboxItemResponse execute(MarkInboxReadCommand c) {
        authorization.requireInboxView(c.workspaceId());
        UUID userId = currentUser.resolveCurrentUser().id();
        WorkInboxItem item = repo.findByIdAndUserId(c.itemId(), userId)
                .orElseThrow(() -> ProductivityExceptions.inboxNotFound(c.itemId()));
        item = repo.save(item.markRead());
        activityLogger.logSuccess(ProductivityEntityTypes.WORK_INBOX, item.id(),
                ProductivityActivityActions.INBOX_READ, "Inbox read");
        return WorkInboxItemResponse.from(item);
    }
}
