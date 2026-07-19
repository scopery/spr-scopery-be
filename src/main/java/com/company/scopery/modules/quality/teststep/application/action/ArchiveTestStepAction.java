package com.company.scopery.modules.quality.teststep.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.teststep.application.response.TestStepResponse;
import com.company.scopery.modules.quality.teststep.domain.model.TestStepRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveTestStepAction {
    private final TestStepRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public ArchiveTestStepAction(TestStepRepository repo, QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public TestStepResponse execute(UUID projectId, UUID id) {
        authorization.requireTestUpdate(projectId);
        var e = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> QualityExceptions.testStepNotFound(id));
        return TestStepResponse.from(repo.save(e.archive(currentUser.resolveCurrentUser().id())));
    }
}
