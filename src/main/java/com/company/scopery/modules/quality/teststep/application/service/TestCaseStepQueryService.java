package com.company.scopery.modules.quality.teststep.application.service;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.teststep.application.response.TestCaseStepResponse;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class TestCaseStepQueryService {
    private final TestCaseStepRepository repo;
    private final QualityAuthorizationService authorization;
    public TestCaseStepQueryService(TestCaseStepRepository repo, QualityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<TestCaseStepResponse> list(UUID projectId, UUID testCaseId) {
        authorization.requireTestView(projectId);
        return repo.findByTestCaseIdOrderBySortOrder(testCaseId).stream()
                .filter(s -> !s.isArchived())
                .map(TestCaseStepResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public TestCaseStepResponse get(UUID projectId, UUID stepId) {
        authorization.requireTestView(projectId);
        return repo.findByIdAndProjectId(stepId, projectId).map(TestCaseStepResponse::from)
                .orElseThrow(() -> QualityExceptions.testCaseStepNotFound(stepId));
    }
}
