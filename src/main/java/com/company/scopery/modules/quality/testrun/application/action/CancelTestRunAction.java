package com.company.scopery.modules.quality.testrun.application.action;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService; import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testrun.application.response.TestRunResponse; import com.company.scopery.modules.quality.testrun.domain.model.TestRunRepository;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CancelTestRunAction {
    private final TestRunRepository repo; private final QualityAuthorizationService authorization;
    public CancelTestRunAction(TestRunRepository repo, QualityAuthorizationService authorization) { this.repo=repo; this.authorization=authorization; }
    @Transactional
    public TestRunResponse execute(UUID projectId, UUID testRunId) {
        authorization.requireTestUpdate(projectId);
        var run = repo.findByIdAndProjectId(testRunId, projectId).orElseThrow(() -> QualityExceptions.testRunNotFound(testRunId));
        return TestRunResponse.from(repo.save(run.cancel()));
    }
}
