package com.company.scopery.modules.quality.testplan.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testplan.application.response.TestPlanResponse;
import com.company.scopery.modules.quality.testplan.domain.model.TestPlanRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveTestPlanAction {
    private final TestPlanRepository repo; private final QualityAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser;
    public ArchiveTestPlanAction(TestPlanRepository repo, QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public TestPlanResponse execute(UUID projectId, UUID testPlanId) {
        authorization.requireTestUpdate(projectId);
        var actor = currentUser.resolveCurrentUser();
        var plan = repo.findByIdAndProjectId(testPlanId, projectId).orElseThrow(() -> QualityExceptions.testPlanNotFound(testPlanId));
        return TestPlanResponse.from(repo.save(plan.archive(actor.id())));
    }
}
