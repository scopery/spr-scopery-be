package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.ImportUseCaseNestedCommand;
import com.company.scopery.modules.traceability.usecase.application.response.ImportUseCaseNestedResponse;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ImportUseCaseNestedAction {

    private final UseCaseRepository useCaseRepo;
    private final TraceabilityAuthorizationService authorization;
    private final ApplyUseCaseNestedPartsAction applyNested;

    public ImportUseCaseNestedAction(
            UseCaseRepository useCaseRepo,
            TraceabilityAuthorizationService authorization,
            ApplyUseCaseNestedPartsAction applyNested) {
        this.useCaseRepo = useCaseRepo;
        this.authorization = authorization;
        this.applyNested = applyNested;
    }

    @Transactional
    public ImportUseCaseNestedResponse execute(ImportUseCaseNestedCommand c) {
        authorization.requireCreate(c.projectId());
        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));
        int createdParts = applyNested.apply(c);
        return new ImportUseCaseNestedResponse(c.useCaseId(), createdParts);
    }
}
