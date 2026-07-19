package com.company.scopery.modules.quality.coverage.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.coverage.application.response.TestCaseCoverageResponse;
import com.company.scopery.modules.quality.coverage.domain.model.TestCaseCoverageRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveTestCaseCoverageAction {
    private final TestCaseCoverageRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public ArchiveTestCaseCoverageAction(TestCaseCoverageRepository repo, QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public TestCaseCoverageResponse execute(UUID projectId, UUID id) {
        authorization.requireTestUpdate(projectId);
        var e = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> QualityExceptions.testCaseNotFound(id));
        return TestCaseCoverageResponse.from(repo.save(e.archive(currentUser.resolveCurrentUser().id())));
    }
}
