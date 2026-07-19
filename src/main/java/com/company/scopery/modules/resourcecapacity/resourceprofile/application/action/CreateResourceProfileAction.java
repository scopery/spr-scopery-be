package com.company.scopery.modules.resourcecapacity.resourceprofile.application.action;
import com.company.scopery.modules.resourcecapacity.resourceprofile.application.command.CreateResourceProfileCommand;
import com.company.scopery.modules.resourcecapacity.resourceprofile.application.response.ResourceProfileResponse;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.enums.ResourceType;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model.ResourceProfile;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model.ResourceProfileRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.util.CapacityEnumParser;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateResourceProfileAction {
    private final ResourceProfileRepository repo; private final CapacityWorkspaceAuthorizationService auth;
    private final CapacityActivityLogger activity;
    public CreateResourceProfileAction(ResourceProfileRepository repo, CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) {
        this.repo=repo; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public ResourceProfileResponse execute(CreateResourceProfileCommand cmd) {
        auth.requireWorkspacePermission(cmd.workspaceId(), IamAuthorities.CAPACITY_PROFILE_CREATE);
        ResourceType type = CapacityEnumParser.parseRequired(ResourceType.class, cmd.resourceType(), "RESOURCE_PROFILE_INVALID_TYPE", "resourceType");
        if (cmd.linkedUserId() != null && repo.existsByWorkspaceIdAndLinkedUserId(cmd.workspaceId(), cmd.linkedUserId())) {
            throw CapacityExceptions.resourceProfileDuplicateUser(cmd.workspaceId(), cmd.linkedUserId());
        }
        ResourceProfile saved = repo.save(ResourceProfile.create(cmd.workspaceId(), type, cmd.displayName(),
                cmd.linkedUserId(), cmd.linkedWorkspaceMemberId(), cmd.primaryRoleId()));
        activity.logSuccess(CapacityEntityTypes.RESOURCE_PROFILE, saved.id(), CapacityActivityActions.RESOURCE_PROFILE_CREATED,
                "Resource profile created: " + saved.displayName());
        return ResourceProfileResponse.from(saved);
    }
}
