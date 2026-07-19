package com.company.scopery.modules.resourcecapacity.effortestimate.application.service;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.effortestimate.application.response.EffortEstimateResponse;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.model.EffortEstimateRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class EffortEstimateQueryService {
    private final EffortEstimateRepository repo; private final ProjectRepository projects; private final CapacityWorkspaceAuthorizationService auth;
    public EffortEstimateQueryService(EffortEstimateRepository repo, ProjectRepository projects, CapacityWorkspaceAuthorizationService auth) {
        this.repo=repo; this.projects=projects; this.auth=auth;
    }
    @Transactional(readOnly=true)
    public List<EffortEstimateResponse> list(UUID projectId) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        return repo.findByProjectId(projectId).stream().map(EffortEstimateResponse::from).toList();
    }
}
