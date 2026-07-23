package com.company.scopery.modules.traceability.nonfunctionalitem.application.action;

import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteNonFunctionalItemAction {

    private final NonFunctionalItemRepository repository;
    private final TraceabilityAuthorizationService authorization;

    public DeleteNonFunctionalItemAction(
            NonFunctionalItemRepository repository,
            TraceabilityAuthorizationService authorization
    ) {
        this.repository = repository;
        this.authorization = authorization;
    }

    @Transactional
    public void execute(UUID id, UUID projectId) {
        authorization.requireCreate(projectId);

        repository.findByIdAndProjectId(id, projectId)
                .orElseThrow(() -> TraceabilityExceptions.nonFunctionalItemNotFound(id));

        repository.delete(id, projectId);
    }
}
