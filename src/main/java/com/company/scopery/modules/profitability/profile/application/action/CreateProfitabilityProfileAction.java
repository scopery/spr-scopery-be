package com.company.scopery.modules.profitability.profile.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.profitability.profile.application.response.ProjectProfitabilityProfileResponse;
import com.company.scopery.modules.profitability.profile.domain.model.*;
import com.company.scopery.modules.profitability.shared.activity.ProfitabilityActivityLogger;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateProfitabilityProfileAction {
    private final ProjectRepository projects; private final ProjectProfitabilityProfileRepository profiles;
    private final ProfitabilityAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser; private final ProfitabilityActivityLogger activityLogger;
    public CreateProfitabilityProfileAction(ProjectRepository projects, ProjectProfitabilityProfileRepository profiles, ProfitabilityAuthorizationService authorization,
                                            CurrentUserAuthorizationService currentUser, ProfitabilityActivityLogger activityLogger) {
        this.projects=projects; this.profiles=profiles; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public ProjectProfitabilityProfileResponse execute(UUID projectId, String currency) {
        authorization.requireUpdate(projectId);
        if (profiles.existsByProjectId(projectId)) throw ProfitabilityExceptions.profileExists(projectId);
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        var p = profiles.save(ProjectProfitabilityProfile.create(project.workspaceId(), project.id(), currency, currentUser.resolveCurrentUser().id()));
        activityLogger.logSuccess("PROFITABILITY_PROFILE", p.id(), "PROFITABILITY_PROFILE_CREATED", "Profile created");
        return ProjectProfitabilityProfileResponse.from(p);
    }
}
