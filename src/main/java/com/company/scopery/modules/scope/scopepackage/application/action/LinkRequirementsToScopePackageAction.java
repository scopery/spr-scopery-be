package com.company.scopery.modules.scope.scopepackage.application.action;

import com.company.scopery.modules.scope.scopepackage.application.command.BulkLinkRequirementsCommand;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import com.company.scopery.modules.traceability.requirement.application.response.RequirementResponse;
import com.company.scopery.modules.traceability.requirement.domain.model.Requirement;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class LinkRequirementsToScopePackageAction {

    private final ScopePackageRepository packages;
    private final RequirementRepository requirements;
    private final ScopeAuthorizationService authorization;

    public LinkRequirementsToScopePackageAction(ScopePackageRepository packages,
                                                 RequirementRepository requirements,
                                                 ScopeAuthorizationService authorization) {
        this.packages = packages;
        this.requirements = requirements;
        this.authorization = authorization;
    }

    @Transactional
    public List<RequirementResponse> execute(BulkLinkRequirementsCommand command) {
        authorization.requireScopeUpdate(command.projectId());
        packages.findByIdAndProjectId(command.packageId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.packageNotFound(command.packageId()));

        List<RequirementResponse> out = new ArrayList<>();
        for (UUID requirementId : command.requirementIds()) {
            Requirement req = requirements.findByIdAndProjectId(requirementId, command.projectId())
                    .orElseThrow(() -> TraceabilityExceptions.requirementNotFound(requirementId));
            Requirement saved = requirements.save(req.withScopePackageId(command.packageId()));
            out.add(RequirementResponse.from(saved));
        }
        return out;
    }
}
