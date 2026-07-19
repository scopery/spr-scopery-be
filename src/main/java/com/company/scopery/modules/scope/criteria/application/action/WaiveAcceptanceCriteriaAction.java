package com.company.scopery.modules.scope.criteria.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.scope.criteria.application.command.WaiveAcceptanceCriteriaCommand;
import com.company.scopery.modules.scope.criteria.application.response.AcceptanceCriteriaResponse;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WaiveAcceptanceCriteriaAction {
    private final AcceptanceCriteriaRepository criteria;
    private final ProjectWorkspaceAuthorizationService projectAuthorization;
    private final CurrentUserAuthorizationService currentUser;

    public WaiveAcceptanceCriteriaAction(AcceptanceCriteriaRepository criteria,
                                         ProjectWorkspaceAuthorizationService projectAuthorization,
                                         CurrentUserAuthorizationService currentUser) {
        this.criteria = criteria;
        this.projectAuthorization = projectAuthorization;
        this.currentUser = currentUser;
    }

    @Transactional
    public AcceptanceCriteriaResponse execute(WaiveAcceptanceCriteriaCommand command) {
        try {
            projectAuthorization.requireProjectPermission(command.projectId(), IamAuthorities.ACCEPTANCE_CRITERIA_WAIVE);
        } catch (RuntimeException ex) {
            throw ScopeExceptions.accessDenied();
        }
        if (command.reason() == null || command.reason().isBlank()) {
            throw ScopeExceptions.reopenReasonRequired();
        }
        var actor = currentUser.resolveCurrentUser();
        var c = criteria.findById(command.criteriaId())
                .orElseThrow(() -> ScopeExceptions.criteriaNotFound(command.criteriaId()));
        if (!c.projectId().equals(command.projectId())) {
            throw ScopeExceptions.criteriaNotFound(command.criteriaId());
        }
        return AcceptanceCriteriaResponse.from(criteria.save(c.waive(actor.id(), command.reason().trim())));
    }
}
