package com.company.scopery.modules.scope.criteria.application.action;

import com.company.scopery.modules.scope.criteria.application.command.CreateAcceptanceCriteriaCommand;
import com.company.scopery.modules.scope.criteria.application.response.AcceptanceCriteriaResponse;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteria;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateAcceptanceCriteriaAction {
    private final DeliverableRepository deliverables;
    private final AcceptanceCriteriaRepository criteria;
    private final ScopeAuthorizationService authorization;

    public CreateAcceptanceCriteriaAction(DeliverableRepository deliverables, AcceptanceCriteriaRepository criteria,
                                          ScopeAuthorizationService authorization) {
        this.deliverables = deliverables;
        this.criteria = criteria;
        this.authorization = authorization;
    }

    @Transactional
    public AcceptanceCriteriaResponse execute(CreateAcceptanceCriteriaCommand command) {
        authorization.requireDeliverableUpdate(command.projectId());
        deliverables.findByIdAndProjectId(command.deliverableId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(command.deliverableId()));
        if (command.title() == null || command.title().isBlank()) {
            throw ScopeExceptions.criteriaTitleRequired();
        }
        String type = command.type() == null || command.type().isBlank() ? "FUNCTIONAL" : command.type();
        boolean mandatory = command.mandatory() == null || command.mandatory();
        AcceptanceCriteria c = criteria.save(AcceptanceCriteria.create(
                command.deliverableId(), command.projectId(), type, command.title().trim(),
                command.description(), mandatory));
        return AcceptanceCriteriaResponse.from(c);
    }
}
