package com.company.scopery.modules.resourcecapacity.resourceprofile.application.action;
import com.company.scopery.modules.resourcecapacity.resourceprofile.application.response.ResourceProfileResponse;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model.ResourceProfileRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveResourceProfileAction {
    private final ResourceProfileRepository repo; private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public ArchiveResourceProfileAction(ResourceProfileRepository repo, CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) {
        this.repo=repo; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public ResourceProfileResponse execute(UUID workspaceId, UUID resourceId, UUID actorId) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_ARCHIVE);
        var profile = repo.findById(resourceId).orElseThrow(() -> CapacityExceptions.resourceProfileNotFound(resourceId));
        if (!profile.workspaceId().equals(workspaceId)) throw CapacityExceptions.resourceProfileNotFound(resourceId);
        var saved = repo.save(profile.archive(actorId));
        activity.logSuccess(CapacityEntityTypes.RESOURCE_PROFILE, saved.id(), CapacityActivityActions.RESOURCE_PROFILE_ARCHIVED, "Archived");
        return ResourceProfileResponse.from(saved);
    }
}
