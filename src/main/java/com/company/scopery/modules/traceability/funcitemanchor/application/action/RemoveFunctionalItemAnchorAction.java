package com.company.scopery.modules.traceability.funcitemanchor.application.action;

import com.company.scopery.modules.traceability.funcitemanchor.domain.model.FunctionalItemAnchorRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RemoveFunctionalItemAnchorAction {

    private final FunctionalItemAnchorRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RemoveFunctionalItemAnchorAction(
            FunctionalItemAnchorRepository repo,
            TraceabilityAuthorizationService authorization
    ) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public void execute(UUID id, UUID functionalItemId, UUID projectId) {
        authorization.requireCreate(projectId);

        repo.findByIdAndFunctionalItemId(id, functionalItemId)
                .orElseThrow(() -> TraceabilityExceptions.funcItemAnchorNotFound(id));

        repo.delete(id, functionalItemId);
    }
}
