package com.company.scopery.modules.scope.criteria.application.action;

import com.company.scopery.modules.scope.criteria.application.command.SatisfyAcceptanceCriteriaCommand;
import com.company.scopery.modules.scope.criteria.application.response.AcceptanceCriteriaResponse;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SatisfyAcceptanceCriteriaAction {
    private final AcceptanceCriteriaRepository criteria;
    private final ScopeAuthorizationService authorization;

    public SatisfyAcceptanceCriteriaAction(AcceptanceCriteriaRepository criteria,
                                           ScopeAuthorizationService authorization) {
        this.criteria = criteria;
        this.authorization = authorization;
    }

    @Transactional
    public AcceptanceCriteriaResponse execute(SatisfyAcceptanceCriteriaCommand command) {
        authorization.requireDeliverableUpdate(command.projectId());
        var c = criteria.findById(command.criteriaId())
                .orElseThrow(() -> ScopeExceptions.criteriaNotFound(command.criteriaId()));
        if (!c.projectId().equals(command.projectId())) {
            throw ScopeExceptions.criteriaNotFound(command.criteriaId());
        }
        return AcceptanceCriteriaResponse.from(criteria.save(c.satisfy()));
    }
}
