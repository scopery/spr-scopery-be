package com.company.scopery.modules.quality.testcase.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService; import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.application.response.TestCaseResponse; import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveTestCaseAction {
    private final TestCaseRepository repo; private final QualityAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser;
    public ArchiveTestCaseAction(TestCaseRepository repo, QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public TestCaseResponse execute(UUID projectId, UUID testCaseId) {
        authorization.requireTestUpdate(projectId);
        var actor = currentUser.resolveCurrentUser();
        var tc = repo.findByIdAndProjectId(testCaseId, projectId).orElseThrow(() -> QualityExceptions.testCaseNotFound(testCaseId));
        return TestCaseResponse.from(repo.save(tc.archive(actor.id())));
    }
}
