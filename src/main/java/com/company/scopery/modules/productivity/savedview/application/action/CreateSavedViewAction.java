package com.company.scopery.modules.productivity.savedview.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.savedview.application.response.SavedViewResponse;
import com.company.scopery.modules.productivity.savedview.domain.model.*;
import com.company.scopery.modules.productivity.shared.activity.ProductivityActivityLogger;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.shared.constant.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.productivity.savedview.application.command.CreateSavedViewCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateSavedViewAction {
    private final SavedViewRepository views; private final ProductivityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final ProductivityActivityLogger activityLogger;
    public CreateSavedViewAction(SavedViewRepository views, ProductivityAuthorizationService authorization, CurrentUserAuthorizationService currentUser, ProductivityActivityLogger activityLogger) {
        this.views=views; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public SavedViewResponse execute(CreateSavedViewCommand c) {
        authorization.requireSavedViewManage(c.workspaceId());
        var user = currentUser.resolveCurrentUser();
        var v = views.save(SavedView.create(c.workspaceId(), c.projectId(), user.id(), c.targetType(), c.name(), c.viewConfigJson(), c.filtersJson()));
        activityLogger.logSuccess(ProductivityEntityTypes.SAVED_VIEW, v.id(), ProductivityActivityActions.SAVED_VIEW_CREATED, "Saved view created");
        return SavedViewResponse.from(v);
    }
}
