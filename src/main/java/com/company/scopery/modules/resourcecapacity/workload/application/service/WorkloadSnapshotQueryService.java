package com.company.scopery.modules.resourcecapacity.workload.application.service;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.workload.application.response.WorkloadSnapshotApiResponse;
import com.company.scopery.modules.resourcecapacity.workload.domain.model.WorkloadSnapshotRepository;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class WorkloadSnapshotQueryService {
    private final WorkloadSnapshotRepository repo; private final ProjectRepository projects;
    private final CapacityWorkspaceAuthorizationService auth;
    public WorkloadSnapshotQueryService(WorkloadSnapshotRepository repo, ProjectRepository projects,
                                        CapacityWorkspaceAuthorizationService auth) {
        this.repo=repo; this.projects=projects; this.auth=auth;
    }
    @Transactional(readOnly=true)
    public List<WorkloadSnapshotApiResponse> list(UUID projectId) {
        var project = projects.findById(projectId).orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(projectId));
        auth.requireWorkspacePermission(project.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        return repo.findByProjectId(projectId).stream().map(WorkloadSnapshotApiResponse::from).toList();
    }
}
