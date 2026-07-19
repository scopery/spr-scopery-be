package com.company.scopery.modules.productivity.savedsearch.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.savedsearch.application.response.SavedSearchResponse;
import com.company.scopery.modules.productivity.savedsearch.domain.model.*;
import com.company.scopery.modules.productivity.shared.activity.ProductivityActivityLogger;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.shared.constant.*;
import com.company.scopery.modules.productivity.shared.error.ProductivityExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.productivity.savedsearch.application.command.CreateSavedSearchCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateSavedSearchAction {
    private final SavedSearchRepository repo; private final ProductivityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final ProductivityActivityLogger activityLogger;
    public CreateSavedSearchAction(SavedSearchRepository repo, ProductivityAuthorizationService authorization, CurrentUserAuthorizationService currentUser, ProductivityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public SavedSearchResponse execute(CreateSavedSearchCommand c) {
        authorization.requireSavedSearchManage(c.workspaceId());
        if (c.name() == null || c.name().isBlank()) throw ProductivityExceptions.nameRequired();
        var user = currentUser.resolveCurrentUser();
        var s = repo.save(SavedSearch.create(c.workspaceId(), c.projectId(), user.id(), c.name().trim(), c.scope(), c.queryText(), c.filtersJson()));
        activityLogger.logSuccess(ProductivityEntityTypes.SAVED_SEARCH, s.id(), ProductivityActivityActions.SAVED_SEARCH_CREATED, "Saved search created");
        return SavedSearchResponse.from(s);
    }
}
