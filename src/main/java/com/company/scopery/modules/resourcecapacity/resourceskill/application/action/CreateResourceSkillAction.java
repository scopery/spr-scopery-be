package com.company.scopery.modules.resourcecapacity.resourceskill.application.action;
import com.company.scopery.modules.resourcecapacity.resourceskill.application.response.ResourceSkillResponse;
import com.company.scopery.modules.resourcecapacity.resourceskill.domain.model.ResourceSkill;
import com.company.scopery.modules.resourcecapacity.resourceskill.domain.model.ResourceSkillRepository;
import com.company.scopery.modules.resourcecapacity.resourceskill.http.request.CreateResourceSkillRequest;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.*;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateResourceSkillAction {
    private final ResourceSkillRepository repo; private final CapacityWorkspaceAuthorizationService auth; private final CapacityActivityLogger activity;
    public CreateResourceSkillAction(ResourceSkillRepository repo, CapacityWorkspaceAuthorizationService auth, CapacityActivityLogger activity) { this.repo=repo; this.auth=auth; this.activity=activity; }
    @Transactional
    public ResourceSkillResponse execute(UUID workspaceId, CreateResourceSkillRequest r) {
        auth.requireWorkspacePermission(workspaceId, IamAuthorities.CAPACITY_PROFILE_CREATE);
        if (repo.existsByWorkspaceIdAndSkillCode(workspaceId, r.skillCode())) throw CapacityExceptions.resourceSkillDuplicate(r.skillCode());
        var saved = repo.save(ResourceSkill.create(workspaceId, r.skillCode(), r.name(), r.description(), r.defaultRateCardId()));
        activity.logSuccess(CapacityEntityTypes.RESOURCE_SKILL, saved.id(), CapacityActivityActions.RESOURCE_SKILL_CREATED, "ResourceSkill created: " + saved.skillCode());
        return ResourceSkillResponse.from(saved);
    }
}
