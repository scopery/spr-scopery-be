package com.company.scopery.modules.resourcecapacity.resourceskill.application.service;
import com.company.scopery.modules.resourcecapacity.resourceskill.application.response.ResourceSkillResponse;
import com.company.scopery.modules.resourcecapacity.resourceskill.domain.model.ResourceSkillRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ResourceSkillQueryService {
    private final ResourceSkillRepository repo; private final CapacityWorkspaceAuthorizationService auth;
    public ResourceSkillQueryService(ResourceSkillRepository repo, CapacityWorkspaceAuthorizationService auth) { this.repo=repo; this.auth=auth; }
    @Transactional(readOnly=true)
    public List<ResourceSkillResponse> list(UUID workspaceId) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_VIEW);
        return repo.findByWorkspaceId(workspaceId).stream().map(ResourceSkillResponse::from).toList();
    }
}
