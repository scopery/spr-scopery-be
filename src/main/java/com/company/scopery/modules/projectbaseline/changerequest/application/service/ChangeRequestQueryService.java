package com.company.scopery.modules.projectbaseline.changerequest.application.service;

import com.company.scopery.modules.projectbaseline.changerequest.application.response.ChangeRequestResponse;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChangeRequestQueryService {
    private final ChangeRequestRepository changeRequests;
    private final ProjectBaselineAuthorizationService authorization;

    public ChangeRequestQueryService(ChangeRequestRepository changeRequests,
                                     ProjectBaselineAuthorizationService authorization) {
        this.changeRequests = changeRequests; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ChangeRequestResponse> list(UUID projectId) {
        authorization.requireChangeRequestView(projectId);
        return changeRequests.findByProjectId(projectId).stream().map(ChangeRequestResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ChangeRequestResponse get(UUID projectId, UUID changeRequestId) {
        authorization.requireChangeRequestView(projectId);
        return changeRequests.findByIdAndProjectId(changeRequestId, projectId)
                .map(ChangeRequestResponse::from)
                .orElseThrow(() -> ProjectBaselineExceptions.changeRequestNotFound(changeRequestId));
    }
}
