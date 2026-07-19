package com.company.scopery.modules.resourcecapacity.resourceprofile.application.service;
import com.company.scopery.modules.resourcecapacity.resourceprofile.application.response.ResourceProfileResponse;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model.ResourceProfileRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ResourceProfileQueryService {
    private final ResourceProfileRepository repo; private final CapacityWorkspaceAuthorizationService auth;
    public ResourceProfileQueryService(ResourceProfileRepository repo, CapacityWorkspaceAuthorizationService auth) {
        this.repo=repo; this.auth=auth;
    }
    @Transactional(readOnly=true)
    public List<ResourceProfileResponse> list(UUID workspaceId) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_VIEW);
        return repo.findByWorkspaceId(workspaceId).stream().map(ResourceProfileResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public ResourceProfileResponse get(UUID workspaceId, UUID resourceId) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_VIEW);
        var p = repo.findById(resourceId).orElseThrow(() -> CapacityExceptions.resourceProfileNotFound(resourceId));
        if (!p.workspaceId().equals(workspaceId)) throw CapacityExceptions.resourceProfileNotFound(resourceId);
        return ResourceProfileResponse.from(p);
    }
}
