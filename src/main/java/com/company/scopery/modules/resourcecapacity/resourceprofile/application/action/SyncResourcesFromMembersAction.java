package com.company.scopery.modules.resourcecapacity.resourceprofile.application.action;
import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.modules.resourcecapacity.resourceprofile.application.response.ResourceProfileResponse;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.enums.ResourceType;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model.ResourceProfile;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model.ResourceProfileRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList; import java.util.List; import java.util.UUID;
@Component
public class SyncResourcesFromMembersAction {
    private final ResourceProfileRepository repo; private final WorkspaceMemberRepository members;
    private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public SyncResourcesFromMembersAction(ResourceProfileRepository repo, WorkspaceMemberRepository members,
                                          CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) {
        this.repo=repo; this.members=members; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public List<ResourceProfileResponse> execute(UUID workspaceId) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_CREATE);
        List<ResourceProfileResponse> created = new ArrayList<>();
        var page = members.findAll(workspaceId, null, WorkspaceMemberStatus.ACTIVE, PageQuery.of(0, 100));
        for (var m : page.content()) {
            if (repo.existsByWorkspaceIdAndLinkedUserId(workspaceId, m.userId())) continue;
            String name = "Member-" + m.userId().toString().substring(0, 8);
            var saved = repo.save(ResourceProfile.create(workspaceId, ResourceType.INTERNAL_USER, name, m.userId(), m.id(), null));
            created.add(ResourceProfileResponse.from(saved));
        }
        activity.logSuccess(CapacityEntityTypes.RESOURCE_PROFILE, workspaceId, CapacityActivityActions.RESOURCE_PROFILE_SYNCED,
                "Synced " + created.size() + " resources from members");
        return created;
    }
}
