package com.company.scopery.modules.productivity.recent.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.recent.application.response.RecentItemResponse;
import com.company.scopery.modules.productivity.recent.domain.model.*;
import com.company.scopery.modules.productivity.shared.activity.ProductivityActivityLogger;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.shared.constant.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.productivity.recent.application.command.RecordRecentItemCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RecordRecentItemAction {
    private final RecentItemRepository recents; private final ProductivityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final ProductivityActivityLogger activityLogger;
    public RecordRecentItemAction(RecentItemRepository recents, ProductivityAuthorizationService authorization, CurrentUserAuthorizationService currentUser, ProductivityActivityLogger activityLogger) {
        this.recents=recents; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public RecentItemResponse execute(RecordRecentItemCommand c) {
        authorization.requireNavView(c.workspaceId());
        var user = currentUser.resolveCurrentUser();
        var r = recents.save(RecentItem.record(c.workspaceId(), user.id(), c.targetType(), c.targetId(), c.title()));
        activityLogger.logSuccess(ProductivityEntityTypes.RECENT, r.id(), ProductivityActivityActions.RECENT_RECORDED, "Recent recorded");
        return RecentItemResponse.from(r);
    }
}
