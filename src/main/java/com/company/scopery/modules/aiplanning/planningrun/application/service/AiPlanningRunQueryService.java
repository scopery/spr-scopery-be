package com.company.scopery.modules.aiplanning.planningrun.application.service;

import com.company.scopery.modules.aiplanning.planningrun.application.response.AiPlanningRunResponse;
import com.company.scopery.modules.aiplanning.planningrun.domain.model.AiPlanningRunRepository;
import com.company.scopery.modules.aiplanning.shared.authorization.AiPlanningAuthorizationService;
import com.company.scopery.modules.aiplanning.shared.error.AiPlanningExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AiPlanningRunQueryService {
    private final AiPlanningRunRepository runs;
    private final AiPlanningAuthorizationService authorization;

    public AiPlanningRunQueryService(AiPlanningRunRepository runs, AiPlanningAuthorizationService authorization) {
        this.runs = runs; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<AiPlanningRunResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return runs.findByProjectId(projectId).stream().map(AiPlanningRunResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AiPlanningRunResponse get(UUID projectId, UUID runId) {
        authorization.requireView(projectId);
        return runs.findByIdAndProjectId(runId, projectId).map(AiPlanningRunResponse::from)
                .orElseThrow(() -> AiPlanningExceptions.runNotFound(runId));
    }
}
