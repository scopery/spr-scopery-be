package com.company.scopery.modules.projectbaseline.changeimpact.application.service;

import com.company.scopery.modules.projectbaseline.changeimpact.application.response.ChangeImpactResponse;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpactRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChangeImpactQueryService {
    private final ChangeImpactRepository impacts;
    private final ProjectBaselineAuthorizationService authorization;

    public ChangeImpactQueryService(ChangeImpactRepository impacts, ProjectBaselineAuthorizationService authorization) {
        this.impacts = impacts; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public ChangeImpactResponse get(UUID projectId, UUID changeRequestId) {
        authorization.requireImpactView(projectId);
        return impacts.findByChangeRequestId(changeRequestId)
                .map(i -> ChangeImpactResponse.from(i, authorization.canViewFinanceImpact(projectId)))
                .orElseThrow(() -> ProjectBaselineExceptions.impactNotFound(changeRequestId));
    }
}
