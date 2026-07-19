package com.company.scopery.modules.resourcecapacity.resourcerole.application.action;
import com.company.scopery.modules.resourcecapacity.resourcerole.application.response.ResourceRoleResponse;
import com.company.scopery.modules.resourcecapacity.resourcerole.domain.model.ResourceRole;
import com.company.scopery.modules.resourcecapacity.resourcerole.domain.model.ResourceRoleRepository;
import com.company.scopery.modules.resourcecapacity.resourcerole.http.request.CreateResourceRoleRequest;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.*;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateResourceRoleAction {
    private final ResourceRoleRepository repo; private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public CreateResourceRoleAction(ResourceRoleRepository repo, CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) { this.repo=repo; this.auth=auth; this.activity=activity; }
    @Transactional
    public ResourceRoleResponse execute(UUID workspaceId, CreateResourceRoleRequest r) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_CREATE);
        if (repo.existsByWorkspaceIdAndRoleCode(workspaceId, r.roleCode())) throw CapacityExceptions.resourceRoleDuplicate(r.roleCode());
        var saved = repo.save(ResourceRole.create(workspaceId, r.roleCode(), r.name(), r.description(), r.defaultRateCardId()));
        activity.logSuccess(CapacityEntityTypes.RESOURCE_ROLE, saved.id(), CapacityActivityActions.RESOURCE_ROLE_CREATED, "ResourceRole created: " + saved.roleCode());
        return ResourceRoleResponse.from(saved);
    }
}
