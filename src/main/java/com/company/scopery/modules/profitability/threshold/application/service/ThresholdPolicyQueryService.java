package com.company.scopery.modules.profitability.threshold.application.service;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.threshold.application.response.ProfitThresholdPolicyResponse;
import com.company.scopery.modules.profitability.threshold.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service("profitabilityThresholdPolicyQueryService")
public class ThresholdPolicyQueryService {
    private final ProjectRepository projects; private final ProfitThresholdPolicyRepository policies; private final ProfitabilityAuthorizationService authorization;
    public ThresholdPolicyQueryService(ProjectRepository projects, ProfitThresholdPolicyRepository policies, ProfitabilityAuthorizationService authorization) {
        this.projects=projects; this.policies=policies; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public ProfitThresholdPolicyResponse get(UUID projectId) {
        authorization.requireView(projectId);
        return policies.findByProjectId(projectId)
                .map(ProfitThresholdPolicyResponse::from)
                .orElseGet(() -> {
                    var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
                    return ProfitThresholdPolicyResponse.from(ProfitThresholdPolicy.defaults(project.workspaceId(), projectId));
                });
    }
}
