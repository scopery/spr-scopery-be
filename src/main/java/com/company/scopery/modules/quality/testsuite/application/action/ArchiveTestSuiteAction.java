package com.company.scopery.modules.quality.testsuite.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.testsuite.application.response.TestSuiteResponse;
import com.company.scopery.modules.quality.testsuite.domain.model.TestSuiteRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveTestSuiteAction {
    private final TestSuiteRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public ArchiveTestSuiteAction(TestSuiteRepository repo, QualityAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser) {
        this.repo = repo; this.authorization = authorization; this.currentUser = currentUser;
    }
    @Transactional
    public TestSuiteResponse execute(UUID projectId, UUID suiteId) {
        authorization.requireTestUpdate(projectId);
        var suite = repo.findByIdAndProjectId(suiteId, projectId).orElseThrow(() -> QualityExceptions.testSuiteNotFound(suiteId));
        return TestSuiteResponse.from(repo.save(suite.archive(currentUser.resolveCurrentUser().id())));
    }
}
