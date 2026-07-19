package com.company.scopery.modules.profitability.threshold.application.action;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.threshold.application.response.ProfitThresholdPolicyResponse;
import com.company.scopery.modules.profitability.threshold.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.util.UUID;
@Component("profitabilityUpsertThresholdPolicyAction")
public class UpsertThresholdPolicyAction {
    private final ProjectRepository projects; private final ProfitThresholdPolicyRepository policies; private final ProfitabilityAuthorizationService authorization;
    public UpsertThresholdPolicyAction(ProjectRepository projects, ProfitThresholdPolicyRepository policies, ProfitabilityAuthorizationService authorization) {
        this.projects=projects; this.policies=policies; this.authorization=authorization;
    }
    @Transactional
    public ProfitThresholdPolicyResponse execute(UUID projectId, BigDecimal healthy, BigDecimal watch, BigDecimal atRisk, BigDecimal lossRisk) {
        authorization.requireUpdate(projectId);
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        var existing = policies.findByProjectId(projectId).orElseGet(() -> ProfitThresholdPolicy.defaults(project.workspaceId(), projectId));
        return ProfitThresholdPolicyResponse.from(policies.save(existing.update(healthy, watch, atRisk, lossRisk)));
    }
}
