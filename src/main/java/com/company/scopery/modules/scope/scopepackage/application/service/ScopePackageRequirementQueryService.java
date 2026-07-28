package com.company.scopery.modules.scope.scopepackage.application.service;

import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import com.company.scopery.modules.traceability.requirement.application.response.RequirementResponse;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ScopePackageRequirementQueryService {

    private final ScopePackageRepository packages;
    private final RequirementRepository requirements;
    private final ScopeAuthorizationService authorization;

    public ScopePackageRequirementQueryService(ScopePackageRepository packages,
                                                RequirementRepository requirements,
                                                ScopeAuthorizationService authorization) {
        this.packages = packages;
        this.requirements = requirements;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RequirementResponse> list(UUID projectId, UUID packageId) {
        authorization.requireScopeView(projectId);
        packages.findByIdAndProjectId(packageId, projectId)
                .orElseThrow(() -> ScopeExceptions.packageNotFound(packageId));
        return requirements.findByProjectIdAndScopePackageId(projectId, packageId).stream()
                .map(RequirementResponse::from)
                .toList();
    }
}
