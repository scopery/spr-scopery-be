package com.company.scopery.modules.resourcecapacity.resourcerole.application.service;
import com.company.scopery.modules.resourcecapacity.resourcerole.application.response.ResourceRoleResponse;
import com.company.scopery.modules.resourcecapacity.resourcerole.domain.model.ResourceRoleRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ResourceRoleQueryService {
    private final ResourceRoleRepository repo; private final CapacityWorkspaceAuthorizationService auth;
    public ResourceRoleQueryService(ResourceRoleRepository repo, CapacityWorkspaceAuthorizationService auth) { this.repo=repo; this.auth=auth; }
    @Transactional(readOnly=true)
    public List<ResourceRoleResponse> list(UUID workspaceId) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_VIEW);
        return repo.findByWorkspaceId(workspaceId).stream().map(ResourceRoleResponse::from).toList();
    }
}
