package com.company.scopery.modules.resourcecapacity.conflict.application.service;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.conflict.application.response.AssignmentConflictApiResponse;
import com.company.scopery.modules.resourcecapacity.conflict.domain.model.AssignmentConflictRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class AssignmentConflictQueryService {
    private final AssignmentConflictRepository repo; private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth;
    public AssignmentConflictQueryService(AssignmentConflictRepository repo, ProjectRepository projects,
                                          CapacityWorkspaceAuthorizationService auth) {
        this.repo=repo; this.projects=projects; this.auth=auth;
    }
    @Transactional(readOnly=true)
    public List<AssignmentConflictApiResponse> list(UUID projectId) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        return repo.findByProjectId(projectId).stream().map(AssignmentConflictApiResponse::from).toList();
    }
}
