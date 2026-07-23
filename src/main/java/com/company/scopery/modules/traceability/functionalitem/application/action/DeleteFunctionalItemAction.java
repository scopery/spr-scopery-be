package com.company.scopery.modules.traceability.functionalitem.application.action;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteFunctionalItemAction {

    private final FunctionalItemRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public DeleteFunctionalItemAction(FunctionalItemRepository repo,
                                      TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public void execute(UUID id, UUID projectId) {
        authorization.requireCreate(projectId);

        repo.findByIdAndProjectId(id, projectId)
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(id));

        repo.delete(id, projectId);
    }
}
