package com.company.scopery.modules.resourcecapacity.actualeffort.application.service;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.actualeffort.application.response.ActualEffortRecordResponse;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.model.ActualEffortRecordRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ActualEffortQueryService {
    private final ActualEffortRecordRepository repo; private final ProjectRepository projects; private final CapacityWorkspaceAuthorizationService auth;
    public ActualEffortQueryService(ActualEffortRecordRepository repo, ProjectRepository projects, CapacityWorkspaceAuthorizationService auth) {
        this.repo=repo; this.projects=projects; this.auth=auth;
    }
    @Transactional(readOnly=true)
    public List<ActualEffortRecordResponse> list(UUID projectId) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        return repo.findByProjectId(projectId).stream().map(ActualEffortRecordResponse::from).toList();
    }
}
